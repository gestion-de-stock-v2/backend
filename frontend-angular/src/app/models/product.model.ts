/** Reflete ProductResponse de stock-service. */
export interface Product {
  id?: number;
  name: string;
  description?: string;
  price: number;
  availableQuantity: number;
  categoryId?: number;
  categoryName?: string;
  categoryDescription?: string;
  supplierId?: number;
  supplierName?: string;
}

/** Reflete ProductRequest de stock-service (creation et mise a jour). */
export interface ProductRequest {
  id?: number;
  name: string;
  description?: string;
  price: number;
  availableQuantity: number;
  categoryId: number;
  supplierId?: number;
}

export interface ProductPurchaseRequest {
  productId: number;
  quantity: number;
}

export interface ProductPurchaseResponse {
  productId: number;
  name: string;
  description?: string;
  price: number;
  quantity: number;
}
