import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { ApiResponse, Product, SaleRequest } from '../types/auth';
import { ToastContainer, toast } from 'react-toastify';

interface CartItem {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export const SalesForm: React.FC = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<number | ''>('');
  const [quantity, setQuantity] = useState('');
  const [cart, setCart] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await api.get<ApiResponse<any>>('/products?page=0&size=100');
      setProducts(response.data.data.content || []);
    } catch (error: any) {
      toast.error('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = () => {
    if (!selectedProduct || !quantity) {
      toast.error('Please select a product and enter quantity');
      return;
    }

    const product = products.find(p => p.id === selectedProduct);
    if (!product) {
      toast.error('Product not found');
      return;
    }

    const qty = parseInt(quantity);
    if (qty <= 0) {
      toast.error('Quantity must be greater than 0');
      return;
    }

    if (qty > product.quantity) {
      toast.error(`Only ${product.quantity} items available`);
      return;
    }

    const existingItem = cart.find(item => item.productId === selectedProduct);
    if (existingItem) {
      const newQty = existingItem.quantity + qty;
      if (newQty > product.quantity) {
        toast.error(`Only ${product.quantity} items available`);
        return;
      }
      setCart(cart.map(item =>
        item.productId === selectedProduct
          ? {
              ...item,
              quantity: newQty,
              subtotal: newQty * item.unitPrice,
            }
          : item
      ));
    } else {
      setCart([
        ...cart,
        {
          productId: product.id,
          productName: product.name,
          quantity: qty,
          unitPrice: product.price,
          subtotal: qty * product.price,
        },
      ]);
    }

    setSelectedProduct('');
    setQuantity('');
    toast.success('Item added to cart');
  };

  const handleRemoveFromCart = (productId: number) => {
    setCart(cart.filter(item => item.productId !== productId));
  };

  const handleCheckout = async () => {
    if (cart.length === 0) {
      toast.error('Cart is empty');
      return;
    }

    try {
      setSubmitting(true);

      const saleRequest = {
        items: cart.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
        })),
      };

      const response = await api.post<ApiResponse<any>>('/sales', saleRequest);
      toast.success('Sale recorded successfully! Receipt sent to email.');
      setCart([]);
      setTimeout(() => navigate('/sales-history'), 2000);
    } catch (error: any) {
      const errorMsg = error.response?.data?.error?.message || 'Failed to record sale';
      toast.error(errorMsg);
    } finally {
      setSubmitting(false);
    }
  };

  const totalAmount = cart.reduce((sum, item) => sum + item.subtotal, 0);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-8">
        <h1 className="text-3xl font-bold mb-8">Record Sale</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Product Selection */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-bold mb-4">Add Products</h2>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Select Product
                  </label>
                  <select
                    value={selectedProduct}
                    onChange={(e) => setSelectedProduct(e.target.value ? parseInt(e.target.value) : '')}
                    className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">-- Select a product --</option>
                    {products.map(product => (
                      <option key={product.id} value={product.id}>
                        {product.name} ({product.quantity} available) - ${product.price.toFixed(2)}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Quantity
                  </label>
                  <input
                    type="number"
                    value={quantity}
                    onChange={(e) => setQuantity(e.target.value)}
                    min="1"
                    className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter quantity"
                  />
                </div>

                <button
                  onClick={handleAddToCart}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                >
                  Add to Cart
                </button>
              </div>
            </div>
          </div>

          {/* Cart Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow p-6 sticky top-8">
              <h2 className="text-xl font-bold mb-4">Order Summary</h2>

              {cart.length === 0 ? (
                <p className="text-gray-600 text-center py-4">Cart is empty</p>
              ) : (
                <>
                  <div className="space-y-3 mb-6 max-h-96 overflow-y-auto">
                    {cart.map(item => (
                      <div key={item.productId} className="flex justify-between items-start pb-3 border-b">
                        <div className="flex-1">
                          <p className="font-medium text-gray-900">{item.productName}</p>
                          <p className="text-sm text-gray-600">
                            {item.quantity} x ${item.unitPrice.toFixed(2)}
                          </p>
                        </div>
                        <div className="text-right">
                          <p className="font-medium text-gray-900">${item.subtotal.toFixed(2)}</p>
                          <button
                            onClick={() => handleRemoveFromCart(item.productId)}
                            className="text-red-600 hover:text-red-900 text-sm mt-1"
                          >
                            Remove
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>

                  <div className="border-t pt-4">
                    <div className="flex justify-between items-center mb-6">
                      <span className="font-bold text-gray-900">Total:</span>
                      <span className="text-2xl font-bold text-blue-600">${totalAmount.toFixed(2)}</span>
                    </div>

                    <button
                      onClick={handleCheckout}
                      disabled={submitting || cart.length === 0}
                      className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded disabled:opacity-50"
                    >
                      {submitting ? 'Processing...' : 'Checkout'}
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default SalesForm;
