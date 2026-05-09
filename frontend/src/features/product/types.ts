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
