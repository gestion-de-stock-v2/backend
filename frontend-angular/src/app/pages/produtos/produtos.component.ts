import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Product, ProductRequest } from '../../models/product.model';
import { Category } from '../../models/category.model';
import { Supplier } from '../../models/supplier.model';
import { IconComponent } from '../../components/icon/icon.component';

const EMPTY_DRAFT: ProductRequest = {
  name: '',
  description: '',
  price: 0,
  availableQuantity: 0,
  categoryId: 0,
  supplierId: undefined,
};

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  templateUrl: './produtos.component.html',
  styleUrls: ['./produtos.component.css'],
})
export class ProdutosComponent implements OnInit {
  products: Product[] = [];
  filtered: Product[] = [];
  categories: Category[] = [];
  suppliers: Supplier[] = [];

  draft: ProductRequest = { ...EMPTY_DRAFT };
  editingId: number | null = null;

  showForm = false;
  loading = false;
  error = '';
  success = '';
  searchTerm = '';

  filter: 'all' | 'low' | 'medium' | 'high' = 'all';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getCategories().subscribe(d => (this.categories = d));
    this.api.getSuppliers().subscribe(d => (this.suppliers = d));
  }

  load(): void {
    this.api.getProducts().subscribe({
      next: d => { this.products = d; this.applyFilter(); },
      error: e => (this.error = e?.error?.message || 'Erreur de chargement'),
    });
  }

  /* ---------------- Filtres ---------------- */
  setFilter(f: 'all' | 'low' | 'medium' | 'high'): void {
    this.filter = f;
    this.applyFilter();
  }

  applyFilter(): void {
    const term = this.searchTerm.toLowerCase().trim();
    let result = this.products;

    if (this.filter === 'low') {
      result = result.filter(p => p.availableQuantity < 5);
    } else if (this.filter === 'medium') {
      result = result.filter(p => p.availableQuantity >= 5 && p.availableQuantity < 20);
    } else if (this.filter === 'high') {
      result = result.filter(p => p.availableQuantity >= 20);
    }

    if (term) {
      result = result.filter(p =>
        p.name?.toLowerCase().includes(term) ||
        p.description?.toLowerCase().includes(term) ||
        p.categoryName?.toLowerCase().includes(term) ||
        p.supplierName?.toLowerCase().includes(term)
      );
    }

    this.filtered = result;
  }

  /* ---------------- Statistiques ---------------- */
  get totalProducts(): number { return this.products.length; }
  get lowStockCount(): number { return this.products.filter(p => p.availableQuantity < 5).length; }
  get mediumStockCount(): number {
    return this.products.filter(p => p.availableQuantity >= 5 && p.availableQuantity < 20).length;
  }
  get highStockCount(): number { return this.products.filter(p => p.availableQuantity >= 20).length; }
  get totalUnits(): number {
    return this.products.reduce((sum, p) => sum + (p.availableQuantity || 0), 0);
  }
  get totalValue(): number {
    return this.products.reduce((sum, p) => sum + (p.price || 0) * (p.availableQuantity || 0), 0);
  }

  /* ---------------- Indicateurs de stock ---------------- */
  getStockClass(qty: number): string {
    if (qty < 5) return 'low';
    if (qty < 20) return 'medium';
    return 'high';
  }

  getStockPercent(qty: number): number {
    const max = 30; // barre pleine a partir de 30 unites
    return Math.min((qty / max) * 100, 100);
  }

  getStockLabel(qty: number): string {
    if (qty === 0) return 'Rupture';
    if (qty < 5) return 'Faible';
    if (qty < 20) return 'Moyen';
    return 'Élevé';
  }

  /* ---------------- CRUD ---------------- */
  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.cancel();
  }

  save(): void {
    this.error = '';
    this.success = '';

    if (!this.draft.name?.trim()) {
      this.error = 'Le nom est obligatoire';
      return;
    }
    if (!this.draft.categoryId) {
      this.error = 'La catégorie est obligatoire';
      return;
    }

    const payload: ProductRequest = {
      ...this.draft,
      price: Number(this.draft.price) || 0,
      availableQuantity: Number(this.draft.availableQuantity) || 0,
      supplierId: this.draft.supplierId || undefined,
    };

    this.loading = true;
    const done = (message: string) => () => {
      this.loading = false;
      this.success = message;
      this.cancel();
      this.load();
    };
    const fail = (fallback: string) => (e: any) => {
      this.loading = false;
      this.error = e?.error?.message || fallback;
    };

    if (this.editingId) {
      this.api.updateProduct(this.editingId, payload)
        .subscribe({ next: done('Produit modifié'), error: fail('Erreur modification') });
    } else {
      this.api.createProduct(payload)
        .subscribe({ next: done('Produit créé'), error: fail('Erreur création') });
    }
  }

  edit(p: Product): void {
    this.editingId = p.id!;
    this.draft = {
      name: p.name,
      description: p.description,
      price: p.price,
      availableQuantity: p.availableQuantity,
      categoryId: p.categoryId ?? 0,
      supplierId: p.supplierId,
    };
    this.showForm = true;
    this.error = '';
    this.success = '';
  }

  remove(id: number): void {
    if (confirm('Supprimer ce produit ?')) {
      this.api.deleteProduct(id).subscribe({
        next: () => this.load(),
        error: e => (this.error = e?.error?.message || 'Erreur suppression'),
      });
    }
  }

  cancel(): void {
    this.editingId = null;
    this.draft = { ...EMPTY_DRAFT };
    this.error = '';
    this.success = '';
  }
}
