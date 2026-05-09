import React, { useState, useEffect } from 'react';
import api from '../../shared/services/api';
import { Product } from '../product/types';
import { SaleItem } from './types';
import { ApiResponse } from '../auth/types';
import { ToastContainer, toast } from 'react-toastify';

interface CartItem {
  product: Product;
  quantity: number;
}

export const SalesFormPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await api.get<ApiResponse<any>>('/products?page=0&size=100');
      setProducts(response.data.data?.content || []);
    } catch {
      toast.error('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const addToCart = (product: Product) => {
    const existing = cart.find(c => c.product.id === product.id);
    if (existing) {
      if (existing.quantity >= product.quantity) {
        toast.warning('Not enough stock');
        return;
      }
      setCart(cart.map(c => c.product.id === product.id ? { ...c, quantity: c.quantity + 1 } : c));
    } else {
      if (product.quantity < 1) { toast.warning('Out of stock'); return; }
      setCart([...cart, { product, quantity: 1 }]);
    }
  };

  const updateQty = (productId: number, quantity: number) => {
    if (quantity < 1) { removeFromCart(productId); return; }
    const product = products.find(p => p.id === productId);
    if (product && quantity > product.quantity) { toast.warning('Not enough stock'); return; }
    setCart(cart.map(c => c.product.id === productId ? { ...c, quantity } : c));
  };

  const removeFromCart = (productId: number) => {
    setCart(cart.filter(c => c.product.id !== productId));
  };

  const totalAmount = cart.reduce((sum, c) => sum + (c.product.price * c.quantity), 0);

  const handleSubmit = async () => {
    if (cart.length === 0) { toast.warning('Cart is empty'); return; }
    try {
      setSubmitting(true);
      const saleRequest = { items: cart.map(c => ({ productId: c.product.id, quantity: c.quantity })) };
      await api.post('/sales', saleRequest);
      toast.success('Sale recorded successfully!');
      setCart([]);
      fetchProducts();
    } catch (error: any) {
      toast.error(error.response?.data?.error?.message || 'Failed to record sale');
    } finally {
      setSubmitting(false);
    }
  };

  const filteredProducts = products.filter(p =>
    p.name.toLowerCase().includes(searchTerm.toLowerCase()) && p.quantity > 0
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-8">
        <h1 className="text-3xl font-bold mb-8">Record Sale</h1>
        <div className="grid grid-cols-3 gap-8">
          <div className="col-span-2">
            <div className="mb-4">
              <input type="text" placeholder="Search products..." value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            {loading ? (
              <div className="text-center py-8">Loading...</div>
            ) : (
              <div className="grid grid-cols-2 gap-4">
                {filteredProducts.map(product => (
                  <div key={product.id} className="bg-white rounded-lg shadow p-4 cursor-pointer hover:shadow-md"
                    onClick={() => addToCart(product)}>
                    <div className="flex items-center space-x-3">
                      {product.imageUrl ? (
                        <img src={product.imageUrl} alt={product.name} className="w-12 h-12 rounded object-cover" />
                      ) : (
                        <div className="w-12 h-12 bg-gray-200 rounded flex items-center justify-center text-gray-400 text-xs">No img</div>
                      )}
                      <div>
                        <p className="font-medium text-gray-900">{product.name}</p>
                        <p className="text-sm text-blue-600">${Number(product.price).toFixed(2)}</p>
                        <p className="text-xs text-gray-500">Stock: {product.quantity}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="bg-white rounded-lg shadow p-6 h-fit">
            <h2 className="text-xl font-bold mb-4">Order Summary</h2>
            {cart.length === 0 ? (
              <p className="text-gray-500 text-sm">No items added yet. Click a product to add it.</p>
            ) : (
              <div className="space-y-3 mb-6">
                {cart.map(({ product, quantity }) => (
                  <div key={product.id} className="flex items-center justify-between">
                    <div className="flex-1">
                      <p className="text-sm font-medium">{product.name}</p>
                      <p className="text-xs text-gray-500">${Number(product.price).toFixed(2)} each</p>
                    </div>
                    <div className="flex items-center space-x-2">
                      <button onClick={() => updateQty(product.id, quantity - 1)} className="w-6 h-6 flex items-center justify-center bg-gray-200 rounded text-sm hover:bg-gray-300">-</button>
                      <span className="text-sm w-6 text-center">{quantity}</span>
                      <button onClick={() => updateQty(product.id, quantity + 1)} className="w-6 h-6 flex items-center justify-center bg-gray-200 rounded text-sm hover:bg-gray-300">+</button>
                      <button onClick={() => removeFromCart(product.id)} className="text-red-500 hover:text-red-700 text-sm ml-1">×</button>
                    </div>
                  </div>
                ))}
              </div>
            )}
            <div className="border-t pt-4 mb-4">
              <div className="flex justify-between font-bold">
                <span>Total</span>
                <span>${totalAmount.toFixed(2)}</span>
              </div>
            </div>
            <button onClick={handleSubmit} disabled={submitting || cart.length === 0}
              className="w-full py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 disabled:bg-gray-400">
              {submitting ? 'Processing...' : 'Complete Sale'}
            </button>
          </div>
        </div>
      </div>
      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default SalesFormPage;
