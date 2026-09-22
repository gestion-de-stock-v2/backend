import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CustomerService } from '../../services/customer.service';
import { OrderService } from '../../services/order.service';
import { IconComponent } from '../../components/icon/icon.component';
import { Product } from '../../models/product.model';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, IconComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  totalProducts = 0;
  totalCustomers = 0;
  totalOrders = 0;
  totalRevenue = 0;
  stockTotal = 0;
  lowStockProducts: Product[] = [];
  recentOrders: Order[] = [];
  loading = true;
  error = '';

  /** Nombre d'appels encore en vol : l'indicateur ne disparaît qu'une fois tous terminés. */
  private pending = 0;

  constructor(
    private productService: ProductService,
    private customerService: CustomerService,
    private orderService: OrderService,
    private router: Router,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.error = '';
    this.loading = true;
    this.pending = 3;

    this.productService.findAll().subscribe({
      next: d => {
        this.totalProducts = d.length;
        this.stockTotal = d.reduce((sum, p) => sum + (p.availableQuantity || 0), 0);
        this.lowStockProducts = d
          .filter(p => (p.availableQuantity || 0) < 10)
          .sort((a, b) => (a.availableQuantity || 0) - (b.availableQuantity || 0))
          .slice(0, 5);
        this.done();
      },
      error: e => this.fail(e, 'Erreur lors du chargement des produits'),
    });

    this.customerService.findAll().subscribe({
      next: d => { this.totalCustomers = d.length; this.done(); },
      error: e => this.fail(e, 'Erreur lors du chargement des clients'),
    });

    this.orderService.findAll().subscribe({
      next: d => {
        this.totalOrders = d.length;
        this.totalRevenue = d.reduce((sum, o) => sum + (o.totalAmount || 0), 0);
        this.recentOrders = [...d]
          .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
          .slice(0, 5);
        this.done();
      },
      error: e => this.fail(e, 'Erreur lors du chargement des commandes'),
    });
  }

  /** Cartes de synthèse : libellé, teinte, valeur et destination au clic. */
  get cards() {
    return [
      { key: 'products',  tone: 'blue',   icon: 'box',      label: 'Produits',           value: String(this.totalProducts),                     route: '/produtos' },
      { key: 'customers', tone: 'indigo', icon: 'user',     label: 'Clients',            value: String(this.totalCustomers),                    route: '/customers' },
      { key: 'orders',    tone: 'orange', icon: 'cart',     label: 'Commandes',          value: String(this.totalOrders),                       route: '/orders' },
      { key: 'stock',     tone: 'green',  icon: 'package',  label: 'Unités en stock',    value: this.format(this.stockTotal),                   route: '/produtos' },
      { key: 'revenue',   tone: 'navy',   icon: 'chart',    label: "Chiffre d'affaires", value: this.format(this.totalRevenue) + ' €',          route: '/orders' },
      { key: 'average',   tone: 'cyan',   icon: 'trending', label: 'Panier moyen',       value: this.format(this.averageOrder) + ' €',          route: '/orders' },
    ];
  }

  private format(n: number): string {
    return new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 }).format(n || 0);
  }

  /** Panier moyen : seule statistique de valeur calculable sans endpoint de paiements. */
  get averageOrder(): number {
    return this.totalOrders > 0 ? this.totalRevenue / this.totalOrders : 0;
  }

  private done(): void {
    if (--this.pending <= 0) this.loading = false;
  }

  private fail(e: any, fallback: string): void {
    this.error = e?.error?.message || fallback;
    this.done();
  }

  goTo(path: string): void { this.router.navigate([path]); }
}
