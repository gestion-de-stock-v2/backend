export type MovementType = 'ENTRY' | 'EXIT';

/** Reflete StockMovementResponse de stock-service. */
export interface StockMovement {
  id?: number;
  type: MovementType;
  quantity: number;
  note?: string;
  createdAt?: string;
  productId: number;
  productName?: string;
  availableQuantityAfter?: number;
}

/** Reflete StockMovementRequest de stock-service. */
export interface StockMovementRequest {
  productId: number;
  type: MovementType;
  quantity: number;
  note?: string;
}
