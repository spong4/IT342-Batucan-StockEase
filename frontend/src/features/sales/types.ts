export interface SaleItem {
  id: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Sale {
  saleId: number;
  totalAmount: number;
  currencyCode: string;
  exchangeRate: number;
  items: SaleItem[];
  createdAt: string;
  receiptEmailSent: boolean;
}
