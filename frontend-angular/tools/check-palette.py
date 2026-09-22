#!/usr/bin/env python3
"""
Verifie que le spectre des roles de DESIGN.md reste lisible et distinguable.

Deux proprietes sont controlees :

  1. Contraste — chaque teinte doit atteindre 4.5:1 (WCAG AA) sur le fond
     effectivement utilise par la pastille, soit un melange a 10 % d'elle-meme
     au-dessus du blanc en theme clair, a 20 % au-dessus de #151E31 en sombre.

  2. Separation — les teintes chromatiques doivent rester a au moins 25 degres
     les unes des autres sur la roue, sans quoi deux roles deviennent
     indiscernables a la taille d'un badge.

Usage :  python3 tools/check-palette.py   (depuis frontend-angular/)
Sortie :  code 0 si tout passe, 1 sinon.
"""

import re
import sys
from pathlib import Path

# Le script vit dans frontend-angular/tools/ : la racine du projet est un cran au-dessus.
ROOT = Path(__file__).resolve().parent.parent
DESIGN = ROOT / "DESIGN.md"
STYLES = ROOT / "src" / "styles.css"

MIN_RATIO = 4.5      # WCAG AA pour du texte de petite taille
MIN_HUE_GAP = 25     # degres
NEUTRAL_MAX_SAT = 30 # en dessous, la teinte compte comme neutre

LIGHT_SURFACE = "#FFFFFF"
DARK_SURFACE = "#151E31"
LIGHT_TINT = 0.10
DARK_TINT = 0.20


def _channels(value: str):
    v = value.lstrip("#")
    return [int(v[i:i + 2], 16) for i in (0, 2, 4)]


def luminance(value: str) -> float:
    out = []
    for c in _channels(value):
        c /= 255
        out.append(c / 12.92 if c <= 0.03928 else ((c + 0.055) / 1.055) ** 2.4)
    return 0.2126 * out[0] + 0.7152 * out[1] + 0.0722 * out[2]


def contrast(a: str, b: str) -> float:
    la, lb = luminance(a), luminance(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)


def mix(fg: str, bg: str, pct: float) -> str:
    """Equivalent de la fonction CSS color-mix(in srgb, fg pct%, bg)."""
    f, b = _channels(fg), _channels(bg)
    return "#" + "".join(f"{round(f[i] * pct + b[i] * (1 - pct)):02X}" for i in range(3))


def hue_sat(value: str):
    r, g, b = [c / 255 for c in _channels(value)]
    mx, mn = max(r, g, b), min(r, g, b)
    light = (mx + mn) / 2
    if mx == mn:
        return None, 0
    d = mx - mn
    sat = d / (2 - mx - mn) if light > 0.5 else d / (mx + mn)
    if mx == r:
        h = ((g - b) / d) % 6
    elif mx == g:
        h = (b - r) / d + 2
    else:
        h = (r - g) / d + 4
    return round(h * 60), round(sat * 100)


def load_spectrum():
    """Extrait les entrees role-* du frontmatter de DESIGN.md."""
    text = DESIGN.read_text(encoding="utf-8")
    found = re.findall(r"^\s*(role-[a-z-]+):\s*'(#[0-9a-fA-F]{6})'", text, re.M)
    if not found:
        sys.exit("Aucune entree role-* trouvee dans DESIGN.md")
    light, dark = {}, {}
    for key, value in found:
        (dark if key.endswith("-dark") else light)[key.replace("-dark", "")] = value.upper()
    return light, dark


def check_implementation(light, dark):
    """
    Verifie que styles.css declare exactement les teintes de DESIGN.md.

    C'est ce controle qui manquait : les couleurs de role avaient derive dans
    quatre composants sans que rien ne le signale.
    """
    print("\nCoherence DESIGN.md <-> styles.css")
    if not STYLES.exists():
        print(f"  {STYLES} introuvable — controle ignore")
        return True

    css = STYLES.read_text(encoding="utf-8")
    # Le bloc clair precede le bloc sombre dans la feuille.
    split = css.find(".dark,")
    blocks = {"clair": css[:split], "sombre": css[split:]} if split > 0 else {"clair": css}

    ok = True
    for scope, palette in (("clair", light), ("sombre", dark)):
        block = blocks.get(scope, "")
        for name, expected in sorted(palette.items()):
            found = re.search(rf"--{re.escape(name)}:\s*(#[0-9a-fA-F]{{6}})", block)
            if not found:
                print(f"  [{scope}] --{name} absent de styles.css")
                ok = False
            elif found.group(1).upper() != expected.upper():
                print(f"  [{scope}] --{name} = {found.group(1).upper()} "
                      f"alors que DESIGN.md indique {expected}")
                ok = False
    if ok:
        print(f"  les {len(light) * 2} teintes declarees correspondent a la charte")
    return ok


def check(label, palette, surface, tint):
    print(f"\n{label}")
    print(f"  {'role':<18}{'teinte':<10}{'fond':<10}{'ratio':<8}{'H':<6}{'S%':<6}verdict")
    ok = True
    for name, tone in sorted(palette.items()):
        background = mix(tone, surface, tint)
        ratio = contrast(tone, background)
        h, s = hue_sat(tone)
        passed = ratio >= MIN_RATIO
        ok &= passed
        print(f"  {name:<18}{tone:<10}{background:<10}{ratio:<8.2f}"
              f"{str(h):<6}{s:<6}{'OK' if passed else 'INSUFFISANT'}")
    return ok


def check_separation(palette):
    print("\nSeparation des teintes")
    chromatic = {n: hue_sat(t)[0] for n, t in palette.items() if hue_sat(t)[1] >= NEUTRAL_MAX_SAT}
    neutral = [n for n, t in palette.items() if hue_sat(t)[1] < NEUTRAL_MAX_SAT]

    hues = sorted(chromatic.values())
    gaps = [(hues[(i + 1) % len(hues)] - hues[i]) % 360 for i in range(len(hues))]
    smallest = min(gaps)

    print(f"  teintes chromatiques : {hues}")
    print(f"  ecarts               : {gaps}")
    print(f"  ecart minimum        : {smallest} deg (seuil {MIN_HUE_GAP})")
    if neutral:
        print(f"  neutres (hors calcul): {', '.join(sorted(neutral))}")

    ok = smallest >= MIN_HUE_GAP
    print(f"  verdict              : {'OK' if ok else 'TROP PROCHES'}")
    return ok


def main() -> int:
    light, dark = load_spectrum()
    if set(light) != set(dark):
        sys.exit("Chaque role doit avoir une variante claire ET une variante sombre")

    ok = check("Theme clair — texte sur fond teinte a 10 % au-dessus du blanc",
               light, LIGHT_SURFACE, LIGHT_TINT)
    ok &= check(f"Theme sombre — texte sur fond teinte a 20 % au-dessus de {DARK_SURFACE}",
                dark, DARK_SURFACE, DARK_TINT)
    ok &= check_separation(light)
    ok &= check_implementation(light, dark)

    print()
    if ok:
        print(f"VERDICT : les {len(light)} roles sont conformes.")
        return 0
    print("VERDICT : au moins un role est non conforme.")
    return 1


if __name__ == "__main__":
    sys.exit(main())
