import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import api from '../../shared/services/api';
import { ApiResponse } from '../auth/types';
import { Product } from './types';
import { ToastContainer, toast } from 'react-toastify';

export const EditProductPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [product, setProduct] = useState<Product | null>(null);
  const [formData, setFormData] = useState({ name: '', description: '', price: '', quantity: '', category: '' });
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  useEffect(() => {
    if (user?.role === 'OWNER') fetchProduct();
  }, [id, user]);

  if (user?.role !== 'OWNER') {
    return (
      <div className="p-8 text-center bg-red-50 min-h-screen">
        <h2 className="text-2xl font-bold text-red-600">Access Denied</h2>
        <p className="text-gray-600 mt-2">Only owners can edit products.</p>
      </div>
    );
  }

  const fetchProduct = async () => {
    try {
      const response = await api.get<ApiResponse<Product>>(`/products/${id}`);
      const fetched = response.data.data!;
      setProduct(fetched);
      setFormData({
        name: fetched.name,
        description: fetched.description || '',
        price: fetched.price.toString(),
        quantity: fetched.quantity.toString(),
        category: fetched.category || '',
      });
    } catch (error: any) {
      toast.error('Failed to load product');
      navigate('/inventory');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) setImageFile(e.target.files[0]);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setSubmitLoading(true);
      const updateData = {
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price),
        quantity: parseInt(formData.quantity),
        category: formData.category,
      };
      await api.put(`/products/${id}`, updateData);
      if (imageFile) {
        const formDataWithImage = new FormData();
        formDataWithImage.append('file', imageFile);
        await api.post(`/products/${id}/upload-image`, formDataWithImage, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
      }
      toast.success('Product updated successfully!');
      setShowSuccess(true);
    } catch (error: any) {
      toast.error(error.response?.data?.error?.message || 'Failed to update product');
    } finally {
      setSubmitLoading(false);
    }
  };

  if (loading) return <div className="p-8 text-center">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-2xl mx-auto p-8">
        <h1 className="text-3xl font-bold mb-8">Edit Product</h1>
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-8">
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">Product Image</label>
            <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
              <input type="file" accept="image/*" onChange={handleFileChange} className="hidden" id="imageInput" />
              <label htmlFor="imageInput" className="cursor-pointer">
                <div className="text-gray-600">{imageFile ? imageFile.name : 'Click to upload new image'}</div>
              </label>
            </div>
          </div>
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">Product Name</label>
            <input type="text" name="name" value={formData.name} onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500" />
          </div>
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
            <textarea name="description" value={formData.description} onChange={handleChange}
              rows={4} className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500" />
          </div>
          <div className="grid grid-cols-2 gap-4 mb-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Price</label>
              <input type="number" name="price" value={formData.price} onChange={handleChange}
                step="0.01" min="0" className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Quantity</label>
              <input type="number" name="quantity" value={formData.quantity} onChange={handleChange}
                min="0" className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500" />
            </div>
          </div>
          <div className="mb-8">
            <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
            <select name="category" value={formData.category} onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500">
              <option value="">Select category</option>
              <option value="Electronics">Electronics</option>
              <option value="Clothing">Clothing</option>
              <option value="Food">Food</option>
              <option value="Other">Other</option>
            </select>
          </div>
          <div className="flex gap-4">
            <button type="button" onClick={() => navigate('/inventory')}
              className="flex-1 px-6 py-3 bg-gray-200 text-gray-800 font-semibold rounded-lg hover:bg-gray-300">Cancel</button>
            <button type="submit" disabled={submitLoading}
              className="flex-1 px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 disabled:bg-gray-400">
              {submitLoading ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
      <ToastContainer position="bottom-right" autoClose={3000} />

      {showSuccess && (
        <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-40">
          <div className="bg-white rounded-2xl shadow-2xl p-10 flex flex-col items-center gap-4 max-w-sm w-full mx-4">
            <div className="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center">
              <svg className="w-10 h-10 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-800">Changes Saved!</h2>
            <p className="text-gray-500 text-center">Your product has been successfully updated.</p>
            <button
              onClick={() => navigate('/inventory')}
              className="mt-2 w-full px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700"
            >
              Go to Inventory
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default EditProductPage;
