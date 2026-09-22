import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Category } from '../models/category.model';
import { Supplier } from '../models/supplier.model';
import { Product, ProductRequest } from '../models/product.model';
import { StockMovement, StockMovementRequest } from '../models/stock-movement.model';

/**
 * Acces au domaine "stock" (produits, categories, fournisseurs, mouvements).
 * Tout passe par la passerelle, qui exige un jeton JWT valide : celui-ci est ajoute
 * automatiquement par authInterceptor.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private base = '/api/v1';

  constructor(private http: HttpClient) {}

  // ---------------- Categories ----------------
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.base}/categories`);
  }
  getCategory(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.base}/categories/${id}`);
  }
  createCategory(c: Category): Observable<Category> {
    return this.http.post<Category>(`${this.base}/categories`, c);
  }
  updateCategory(id: number, c: Category): Observable<Category> {
    return this.http.put<Category>(`${this.base}/categories/${id}`, c);
  }
  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/categories/${id}`);
  }

  // ---------------- Fournisseurs ----------------
  getSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(`${this.base}/suppliers`);
  }
  getSupplier(id: number): Observable<Supplier> {
    return this.http.get<Supplier>(`${this.base}/suppliers/${id}`);
  }
  createSupplier(s: Supplier): Observable<Supplier> {
    return this.http.post<Supplier>(`${this.base}/suppliers`, s);
  }
  updateSupplier(id: number, s: Supplier): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.base}/suppliers/${id}`, s);
  }
  deleteSupplier(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/suppliers/${id}`);
  }

  // ---------------- Produits ----------------
  getProducts(): Observable<Product[]> {
    // Tolere une reponse paginee Spring aussi bien qu'un tableau simple.
    return this.http.get<any>(`${this.base}/products`).pipe(
      map(res => (Array.isArray(res) ? res : (res?.content ?? [])))
    );
  }
  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.base}/products/${id}`);
  }
  createProduct(p: ProductRequest): Observable<number> {
    return this.http.post<number>(`${this.base}/products`, p);
  }
  updateProduct(id: number, p: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.base}/products/${id}`, p);
  }
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/products/${id}`);
  }

  // ---------------- Mouvements de stock ----------------
  getStockMovements(productId?: number): Observable<StockMovement[]> {
    const url = productId
      ? `${this.base}/stock-movements/product/${productId}`
      : `${this.base}/stock-movements`;
    return this.http.get<any>(url).pipe(
      map(res => (Array.isArray(res) ? res : (res?.content ?? [])))
    );
  }
  createStockMovement(m: StockMovementRequest): Observable<StockMovement> {
    return this.http.post<StockMovement>(`${this.base}/stock-movements`, m);
  }
}
