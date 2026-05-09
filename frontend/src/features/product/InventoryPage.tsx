import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import api from '../../shared/services/api';
import { Product } from './types';
import { ApiResponse } from '../auth/types';
import { ToastContainer, toast } from 'react-toastify';

export const InventoryPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');

  useEffect(() => {
    fetchProducts();
  }, [categoryFilter]);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const url = categoryFilter
        ? `/products?page=0&size=20&category=${categoryFilter}`
        : '/products?page=0&size=20';
      const response = await api.get<ApiResponse<any>>(url);
      setProducts(response.data.data?.content || []);
    } catch (error: any) {
      toast.error('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (productId: number) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    try {
      await api.delete(`/products/${productId}`);
      toast.success('Product deleted');
      setProducts(products.filter(p => p.id !== productId));
    } catch (error: any) {
      toast.error('Failed to delete product');
    }
  };

  const filteredProducts = products.filter(p =>
    p.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold">Inventory</h1>
          {user?.role === 'OWNER' && (
            <Link to="/products/add"
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-2 rounded-lg">
              + Add Product
            </Link>
          )}
        </div>

        <div className="flex gap-4 mb-6">
          <input
            type="text"
            placeholder="Search products..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Categories</option>
            <option value="Electronics">Electronics</option>
            <option value="Clothing">Clothing</option>
            <option value="Food">Food</option>
            <option value="Other">Other</option>
          </select>
        </div>

        {loading ? (
          <div className="text-center py-8">Loading...</div>
        ) : filteredProducts.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-8 text-center">
            <p className="text-gray-600">No products found</p>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Product</th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Category</th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Price</th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Quantity</th>
                  {user?.role === 'OWNER' && (
                    <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Actions</th>
                  )}
                </tr>
              </thead>
              <tbody className="divide-y">
                {filteredProducts.map(product => (
                  <tr key={product.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4">
                      <div className="flex items-center space-x-3">
                        {product.imageUrl ? (
                          <img src={product.imageUrl} alt={product.name} className="w-10 h-10 rounded object-cover" />
                        ) : (
                          <div className="w-10 h-10 bg-gray-200 rounded flex items-center justify-center text-gray-400 text-xs">No img</div>
                        )}
                        <div>
                          <p className="font-medium text-gray-900">{product.name}</p>
                          <p className="text-sm text-gray-500">{product.description}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{product.category || '-'}</td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">${Number(product.price).toFixed(2)}</td>
                    <td className="px-6 py-4">
                      <span className={`text-sm font-medium ${product.quantity < 10 ? 'text-red-600' : 'text-gray-900'}`}>
                        {product.quantity}
                      </span>
                    </td>
                    {user?.role === 'OWNER' && (
                      <td className="px-6 py-4 text-sm space-x-2">
                        <button onClick={() => navigate(`/products/${product.id}/edit`)} className="text-blue-600 hover:text-blue-900">Edit</button>
                        <button onClick={() => handleDelete(product.id)} className="text-red-600 hover:text-red-900">Delete</button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default InventoryPage;
