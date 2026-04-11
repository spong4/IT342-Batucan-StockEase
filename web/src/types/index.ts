// Auth Types
export interface User {
  id: number;
  email: string;
  firstname: string;
  lastname: string;
  role: 'OWNER' | 'STAFF';
}

export interface RegisterData {
  email: string;
  password: string;
  firstname: string;
  lastname: string;
  role: 'OWNER' | 'STAFF';
}

export interface LoginData {
  email: string;
  password: string;
}

// Product Types
export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: string;
  imageUrl: string | null;
  createdById: number;
}

export interface ProductFormData {
  name: string;
  description: string;
  price: number;
  quantity: number;
  category: string;
}

// Sale Types
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

export interface SaleItemRequest {
  productId: number;
  quantity: number;
}

// API Response Type
export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: {
    code: string;
    message: string;
    details: any;
  } | null;
  timestamp: string;
}
