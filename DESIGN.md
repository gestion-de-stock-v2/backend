---
name: Modern Electric Mobile
colors:
  surface: '#faf8ff'
  surface-dim: '#d2d9f4'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3ff'
  surface-container: '#eaedff'
  surface-container-high: '#e2e7ff'
  surface-container-highest: '#dae2fd'
  on-surface: '#131b2e'
  on-surface-variant: '#434655'
  inverse-surface: '#283044'
  inverse-on-surface: '#eef0ff'
  outline: '#747686'
  outline-variant: '#c4c5d7'
  surface-tint: '#2151da'
  primary: '#0037b0'
  on-primary: '#ffffff'
  primary-container: '#1d4ed8'
  on-primary-container: '#cad3ff'
  inverse-primary: '#b7c4ff'
  secondary: '#a73a00'
  on-secondary: '#ffffff'
  secondary-container: '#fd651e'
  on-secondary-container: '#571a00'
  tertiary: '#003ca3'
  on-tertiary: '#ffffff'
  tertiary-container: '#0051d6'
  on-tertiary-container: '#c9d4ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b7c4ff'
  on-primary-fixed: '#001551'
  on-primary-fixed-variant: '#0039b5'
  secondary-fixed: '#ffdbce'
  secondary-fixed-dim: '#ffb599'
  on-secondary-fixed: '#370e00'
  on-secondary-fixed-variant: '#7f2b00'
  tertiary-fixed: '#dbe1ff'
  tertiary-fixed-dim: '#b4c5ff'
  on-tertiary-fixed: '#00174b'
  on-tertiary-fixed-variant: '#003ea8'
  background: '#faf8ff'
  on-background: '#131b2e'
  surface-variant: '#dae2fd'
typography:
  headline-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 36px
    fontWeight: '800'
    lineHeight: 44px
    letterSpacing: -0.03em
  headline-xl-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 34px
    letterSpacing: -0.025em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 30px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 26px
    letterSpacing: -0.015em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: -0.005em
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: 0em
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system embodies high-craft digital product design for modern mobile experiences. It merges structural precision with energetic sophistication—tailored for contemporary fintech, high-velocity SaaS companions, premium lifestyle platforms, and specialized mobile workflows.

The visual direction blends **Corporate Modern** rigor with **Tactile Subtlety**:
- **Trust & Fluidity:** Deep royal blues evoke security, architectural balance, and authority, while bright amber accents inject purposeful vitality without visual noise.
- **Mobile Ergonomics:** Every interactive component prioritizes touch precision, thumb-reach accessibility, and tactile clarity through low-latency feedback states and clear spatial hierarchy.
- **Refined Materiality:** The interface avoids both flat sterility and skeuomorphic excess, employing layered neutral planes, diffused blue-tinted drop shadows, and delicate 1px perimeter outlines for crisp delineation under direct sunlight or low-light conditions.

## Colors

The palette establishes an intentional contrast between institutional trust and decisive calls-to-action.

### Primary Spectrum (Royal & Electric Blue)
- **Primary Base (`#1D4ED8`):** Anchors main navigational landmarks, active tabs, header actions, and brand touchpoints.
- **Primary Bright (`#2563EB`):** Applied to interactive interactive hover/active layers, key iconography, and selected list item borders.
- **Primary Tint (`#EFF6FF`):** Serves as contextual fills for chips, informational highlights, and selected item backdrops.

### Accent Spectrum (Burnt Tangerine & Warm Amber)
- **Secondary Base (`#EA580C`):** Reserved for high-value focal moments: conversion-critical buttons, urgent status indicators, and notification badges.
- **Secondary Bright (`#F97316`):** Used for micro-animations, active toggles in promotional states, and pressed states of accent buttons.
- **Secondary Tint (`#FFF7ED`):** Backing fill for warning alerts, pending transactions, and accent badge containers.

### Neutrals & Surfaces (Deep Slate to Off-White)
- **Neutral Darkest (`#0F172A`):** The primary typographic ink and icon shade, offering high-legibility contrast against light canvases.
- **Neutral Dark (`#1E293B`):** Secondary text, subtle subheadings, and structural lines.
- **Neutral Mid (`#64748B`):** Supporting metadata, placeholder values, and inactive icon states.
- **Neutral Border (`#E2E8F0`):** 1px delineators across cards, modular dividers, and unselected fields.
- **Neutral Surface / Off-White (`#F8FAFC`):** The master canvas tone, creating soft distinction beneath pure white cards.
- **Pure White (`#FFFFFF`):** High-elevation cards, modals, sheets, and active button typography.

## Typography

The type system is powered by **Plus Jakarta Sans**, offering geometric precision paired with friendly aperture curves optimized for mobile renderers and varying viewing angles.

### Typographic Rules
- **Display & Large Headers:** Utilize negative tracking (`-0.02em` to `-0.03em`) and heavy weights (`700`, `800`) to provide firm architectural grounding above scrolling feeds.
- **Body Text:** Uses standard weights (`400`) and proportional line heights (`1.4` to `1.5`) to preserve comfortable reading cadences in compact cards.
- **Labels & Microcopy:** Leverage medium and bold weights (`600`, `700`) paired with subtle positive tracking for tags, navigation titles, badges, and uppercase category markers.

## Layout & Spacing

The layout is built upon an 8-point base grid with a 4-point micro-mesh for fine-grained internal alignments (icons, text offsets, badges).

### Mobile Grid Architecture
- **Canvas Margins:** Fixed `16px` (`margin: 1rem`) screen padding on compact mobile form factors (`< 640px`), expanding to `24px` on tablets (`≥ 768px`).
- **Column System:** 4-column fluid layout on mobile, transitioning to 8 columns on tablet viewports.
- **Touch Target Safeguards:** All standalone touchable elements (icon buttons, toggles, tab items) conform to a strict minimum bounding box of `44x44px`, regardless of visual icon size.
- **Bottom Clearance Rule:** The lower scroll view must include an additional safe-area buffer of at least `88px` to ensure interface elements are never obscured by the persistent Bottom Tab Bar and mobile home indicators.

## Elevation & Depth

Visual stratification uses a hybrid approach of delicate 1px boundary lines and color-cast ambient shadows.

### Elevation Hierarchy
- **Level 0 (Base Canvas):** `#F8FAFC`. Completely flat.
- **Level 1 (Cards, List Groups):** `#FFFFFF`. Bound by a 1px solid border of `#E2E8F0` with a subtle diffuse drop shadow: `box-shadow: 0 2px 8px -2px rgba(15, 23, 42, 0.04), 0 1px 3px -1px rgba(15, 23, 42, 0.06)`.
- **Level 2 (Floating Action Buttons, Menus, Popovers):** `#FFFFFF`. Bordered by `#CBD5E1` with a targeted shadow: `box-shadow: 0 10px 25px -5px rgba(29, 78, 216, 0.08), 0 8px 10px -6px rgba(15, 23, 42, 0.04)`.
- **Level 3 (Modals, Bottom Action Sheets):** `#FFFFFF`. Supported by an interactive backdrop scrim (`rgba(15, 23, 42, 0.45)`) and elevated with: `box-shadow: 0 20px 35px -10px rgba(15, 23, 42, 0.20)`.
- **Level 4 (Bottom Tab Bar):** `#FFFFFF` with 92% opacity and a `16px` backdrop-filter blur (`blur(16px)`), separated from the scroll plane by a 1px border-top (`#E2E8F0`).

## Shapes

The interface embraces generous, smooth radii to convey modern friendliness while maintaining balanced proportions:
- **Base Rounding (`rounded`, 8px):** Checkboxes, inline code badges, secondary micro-controls.
- **Component Rounding (`rounded-lg`, 12px - 14px):** Form input fields, buttons, segmented controls, list row highlight containers.
- **Surface Rounding (`rounded-xl`, 16px - 20px):** Information cards, stat panels, modal containers, and bottom sheets (top-left & top-right).
- **Pill Rounding (`rounded-full`, 9999px):** Status badges, chips, numerical notification counters, and primary FAB buttons.

## Components

### Buttons
- **Primary Action:** Solid Electric Blue (`#1D4ED8`), high-contrast white text (`#FFFFFF`), `14px` radius (`rounded-xl`), height `48px`. Active state transitions to `#1E40AF` with a subtle scale down (`transform: scale(0.98)`).
- **Accent Action:** Solid Orange (`#EA580C`), high-contrast white text, reserved for conversion endpoints and urgent tasks. Active state shifts to `#C2410C`.
- **Secondary / Ghost:** Transparent or pure white background with a 1px `#E2E8F0` border and `#0F172A` text. Active state triggers a tint fill (`#F1F5F9`).

### Inputs & Form Fields
- Height `48px`, background `#FFFFFF`, border `1px solid #CBD5E1`, border-radius `12px`.
- **Focus State:** 1px border `#1D4ED8` with an ambient glow ring (`box-shadow: 0 0 0 3px rgba(29, 78, 216, 0.15)`).
- **Labeling:** Floating or stacked above the field using `label-md` (`#0F172A`), with inline validation icons right-aligned.

### Cards
- Pure white fill (`#FFFFFF`), radius `16px`, 1px perimeter border in `#E2E8F0`. Internal padding follows `16px` (`space-md`) or `20px`.
- Interactive cards receive a hover/active border transition to `#93C5FD` and an elevated shadow lift.

### Chips & Badges
- **Status Chips:** Height `26px`, radius `9999px`, horizontal padding `10px`. 
- **Active / Accent Badge:** Fill `#FFF7ED`, text `#EA580C`, with a 6px circular indicator dot.
- **Primary Filter Chip:** Unselected is `#FFFFFF` with `#E2E8F0` border; selected is `#EFF6FF` with `#1D4ED8` border and `#1D4ED8` text.

### Bottom Tab Bar
- Anchored to the viewport bottom with safe-area spacing. Height `64px` + bottom system margin.
- Translucent white surface (`rgba(255, 255, 255, 0.92)` with `backdrop-filter: blur(16px)`).
- Unselected tab items use `#64748B`; active tab item highlights in `#1D4ED8` with a 4px indicator bar or tinted pill background.

### Selection Controls (Checkboxes & Radios)
- Bounding size `20x20px`. Checkboxes use `6px` radius; Radios use full circular radius.
- Unchecked: `1.5px solid #CBD5E1`. Checked: Solid `#1D4ED8` with a white checkmark or center pip.