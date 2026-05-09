import React, { useState, useEffect } from 'react';
import { useAuth } from '../auth/AuthContext';
import api from '../../shared/services/api';
import { ApiResponse } from '../auth/types';
import { ToastContainer, toast } from 'react-toastify';

interface DashboardStats {
  totalSales: number;
  totalProducts: number;
  totalTransactions: number;
  role: string;
}

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats>({ totalSales: 0, totalProducts: 0, totalTransactions: 0, role: '' });
  const [latestSales, setLatestSales] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [productsRes, salesRes] = await Promise.all([
        api.get<ApiResponse<any>>('/products?page=0&size=100'),
        api.get<ApiResponse<any>>('/sales?page=0&size=5'),
      ]);
      const products = productsRes.data.data?.content || [];
      const salesPage = salesRes.data.data?.content || [];
      const totalSalesAmount = salesPage.reduce((sum: number, s: any) => sum + Number(s.totalAmount || 0), 0);

      setStats({
        totalSales: totalSalesAmount,
        totalProducts: products.length,
        totalTransactions: salesRes.data.data?.totalElements || 0,
        role: user?.role || '',
      });
      setLatestSales(salesPage.slice(0, 5));
    } catch {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold">Dashboard</h1>
          <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">{user?.role}</span>
        </div>

        {loading ? (
          <div className="text-center py-8">Loading...</div>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-500">Total Sales</p>
                <p className="text-3xl font-bold text-gray-900 mt-1">${stats.totalSales.toFixed(2)}</p>
              </div>
              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-500">Total Products</p>
                <p className="text-3xl font-bold text-gray-900 mt-1">{stats.totalProducts}</p>
              </div>
              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-500">Total Transactions</p>
                <p className="text-3xl font-bold text-gray-900 mt-1">{stats.totalTransactions}</p>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow">
              <div className="px-6 py-4 border-b">
                <h2 className="text-lg font-bold">Latest Sales</h2>
              </div>
              {latestSales.length === 0 ? (
                <div className="p-6 text-gray-500 text-center">No sales yet</div>
              ) : (
                <table className="min-w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Sale ID</th>
                      <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Date</th>
                      <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Items</th>
                      <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Total</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y">
                    {latestSales.map((sale: any) => (
                      <tr key={sale.saleId} className="hover:bg-gray-50">
                        <td className="px-6 py-4 text-sm font-medium text-gray-900">#{sale.saleId}</td>
                        <td className="px-6 py-4 text-sm text-gray-600">
                          {new Date(sale.createdAt).toLocaleDateString()}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-600">{sale.items?.length || 0}</td>
                        <td className="px-6 py-4 text-sm font-medium">${Number(sale.totalAmount).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </>
        )}
      </div>
      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default DashboardPage;
