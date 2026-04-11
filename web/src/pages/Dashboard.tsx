import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import { ApiResponse, Sale } from '../types/auth';

export const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalSales: 0,
    totalProducts: 0,
    totalTransactions: 0,
    staffCount: 0,
  });
  const [latestSales, setLatestSales] = useState<Sale[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      // Fetch sales data
      const salesResponse = await api.get<ApiResponse<any>>('/sales?page=0&size=5');
      const sales = salesResponse.data.data.content || [];
      setLatestSales(sales);

      // Calculate stats from sales data
      const totalSales = sales.reduce((acc, sale) => acc + Number(sale.totalAmount), 0);
      
      // Fetch products data
      const productsResponse = await api.get<ApiResponse<any>>('/products?page=0&size=100');
      const products = productsResponse.data.data.content || [];

      setStats({
        totalSales,
        totalProducts: products.length,
        totalTransactions: sales.length,
        staffCount: 1, // Simplified for demo
      });
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="p-8 text-center">Loading dashboard...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-8">
        <h1 className="text-4xl font-bold mb-8">Dashboard</h1>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-600 text-sm font-medium">Total Sales</h3>
            <p className="text-3xl font-bold mt-2">${stats.totalSales.toFixed(2)}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-600 text-sm font-medium">Products</h3>
            <p className="text-3xl font-bold mt-2">{stats.totalProducts}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-600 text-sm font-medium">Transactions</h3>
            <p className="text-3xl font-bold mt-2">{stats.totalTransactions}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-600 text-sm font-medium">Role</h3>
            <p className="text-3xl font-bold mt-2">{user?.role}</p>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-2xl font-bold mb-4">Welcome, {user?.firstname}!</h2>
          <p className="text-gray-600">
            Use the navigation menu to access inventory, sales, and other features.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
