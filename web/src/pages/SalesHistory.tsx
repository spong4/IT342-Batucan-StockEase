import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { ApiResponse, Sale } from '../types/auth';
import { ToastContainer, toast } from 'react-toastify';

export const SalesHistory: React.FC = () => {
  const [sales, setSales] = useState<Sale[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [selectedSale, setSelectedSale] = useState<Sale | null>(null);

  useEffect(() => {
    fetchSales();
  }, [page]);

  const fetchSales = async () => {
    try {
      setLoading(true);
      const response = await api.get<ApiResponse<any>>(`/sales?page=${page}&size=10`);
      setSales(response.data.data.content || []);
    } catch (error: any) {
      toast.error('Failed to load sales history');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-8">
        <h1 className="text-3xl font-bold mb-8">Sales History</h1>

        {loading ? (
          <div className="text-center py-8">Loading...</div>
        ) : sales.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-8 text-center">
            <p className="text-gray-600">No sales found</p>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Date</th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Amount</th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Currency</th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Items</th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {sales.map(sale => (
                  <tr key={sale.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm text-gray-900">
                      {new Date(sale.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                      ${sale.totalAmount.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {sale.currencyCode === 'USD' ? '$' : '₱'} {sale.currencyCode}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {sale.items.length} item{sale.items.length !== 1 ? 's' : ''}
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <button
                        onClick={() => setSelectedSale(sale)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        View Details
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        <div className="flex justify-between mt-6">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50"
          >
            Previous
          </button>
          <button
            onClick={() => setPage(page + 1)}
            disabled={sales.length < 10}
            className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50"
          >
            Next
          </button>
        </div>

        {/* Modal for sale details */}
        {selectedSale && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg shadow-lg p-8 max-w-md w-full">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Sale Details</h2>
                <button
                  onClick={() => setSelectedSale(null)}
                  className="text-gray-500 hover:text-gray-700 text-2xl"
                >
                  ×
                </button>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="text-sm font-medium text-gray-700">Sale ID</label>
                  <p className="text-gray-900">{selectedSale.id}</p>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-700">Date</label>
                  <p className="text-gray-900">
                    {new Date(selectedSale.createdAt).toLocaleString()}
                  </p>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-700">Total Amount</label>
                  <p className="text-2xl font-bold text-blue-600">
                    ${selectedSale.totalAmount.toFixed(2)}
                  </p>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-700 mb-2 block">Items</label>
                  <div className="space-y-2 max-h-48 overflow-y-auto">
                    {selectedSale.items.map((item, idx) => (
                      <div key={idx} className="text-sm border-b pb-2">
                        <p className="font-medium">{item.productName}</p>
                        <p className="text-gray-600">
                          {item.quantity} x ${item.unitPrice.toFixed(2)} = ${item.subtotal.toFixed(2)}
                        </p>
                      </div>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="text-sm font-medium text-gray-700">Receipt Email</label>
                  <p className="text-gray-900">
                    {selectedSale.receiptEmailSent ? '✓ Sent' : '✗ Not sent'}
                  </p>
                </div>
              </div>

              <button
                onClick={() => setSelectedSale(null)}
                className="w-full mt-6 bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
              >
                Close
              </button>
            </div>
          </div>
        )}
      </div>
      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default SalesHistory;
