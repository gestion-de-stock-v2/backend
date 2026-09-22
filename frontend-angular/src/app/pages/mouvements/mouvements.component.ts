import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { MovementType, StockMovement, StockMovementRequest } from '../../models/stock-movement.model';
import { Product } from '../../models/product.model';
import { IconComponent } from '../../components/icon/icon.component';

@Component({
  selector: 'app-mouvements',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  templateUrl: './mouvements.component.html',
  styleUrls: ['./mouvements.component.css'],
})
export class MouvementsComponent implements OnInit {
  products: Product[] = [];
  movements: StockMovement[] = [];
  selectedProductId: number | null = null;

  type: MovementType = 'ENTRY';
  quantity = 1;
  note = '';
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.loadProducts(true); }

  private loadProducts(selectFirst = false): void {
    this.api.getProducts().subscribe(d => {
      this.products = d;
      if (selectFirst && d.length) {
        this.selectedProductId = d[0].id!;
        this.load();
      }
    });
  }

  load(): void {
    if (!this.selectedProductId) return;
    this.api.getStockMovements(this.selectedProductId).subscribe(d => (this.movements = d));
  }

  save(): void {
    this.error = '';
    if (!this.selectedProductId) return;

    const payload: StockMovementRequest = {
      productId: this.selectedProductId,
      type: this.type,
      quantity: Number(this.quantity) || 0,
      note: this.note,
    };

    this.api.createStockMovement(payload).subscribe({
      next: () => {
        this.note = '';
        this.load();
        // La quantite disponible a change : recharger le catalogue.
        this.loadProducts();
      },
      error: e => (this.error = e?.error?.message || 'Erreur : stock insuffisant'),
    });
  }
}
