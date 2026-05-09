import React, { useState, useEffect } from 'react';
import api from '../../shared/services/api';
import { Sale } from './types';
import { ApiResponse } from '../auth/types';
import { ToastContainer, toast } from 'react-toastify';

export const SalesHistoryPage: React.FC = () => {
  const [sales, setSales] = useState<Sale[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedSale, setSelectedSale] = useState<Sale | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchSales();
  }, [currentPage]);

  const fetchSales = async () => {
    try {
      setLoading(true);
      const response = await api.get<ApiResponse<any>>(`/sales?page=${currentPage}&size=10`);
      const pageData = response.data.data;
      setSales(pageData?.content || []);
      setTotalPages(pageData?.totalPages || 0);
    } catch {
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
            <p className="text-gray-600">No sales records found</p>
          </div>
        ) : (
          <>
            <div className="bg-white rounded-lg shadow overflow-hidden">
              <table className="min-w-full">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Sale ID</th>
                    <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Date</th>
                    <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Items</th>
                    <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Total Amount</th>
                    <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Receipt Sent</th>
                    <th className="px-6 py-3 text-left text-sm font-medium text-gray-900">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y">
                  {sales.map(sale => (
                    <tr key={sale.saleId} className="hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm font-medium text-gray-900">#{sale.saleId}</td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {new Date(sale.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">{sale.items?.length || 0} items</td>
                      <td className="px-6 py-4 text-sm font-medium text-gray-900">${Number(sale.totalAmount).toFixed(2)}</td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${sale.receiptEmailSent ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'}`}>
                          {sale.receiptEmailSent ? 'Sent' : 'Not Sent'}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <button onClick={() => setSelectedSale(sale)}
                          className="text-blue-600 hover:text-blue-900 text-sm">View Details</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {totalPages > 1 && (
              <div className="flex justify-center mt-6 space-x-2">
                <button onClick={() => setCurrentPage(p => Math.max(0, p - 1))} disabled={currentPage === 0}
                  className="px-4 py-2 bg-white border border-gray-300 rounded disabled:opacity-50 hover:bg-gray-50">Previous</button>
                <span className="px-4 py-2 bg-blue-600 text-white rounded">{currentPage + 1} / {totalPages}</span>
                <button onClick={() => setCurrentPage(p => Math.min(totalPages - 1, p + 1))} disabled={currentPage >= totalPages - 1}
                  className="px-4 py-2 bg-white border border-gray-300 rounded disabled:opacity-50 hover:bg-gray-50">Next</button>
              </div>
            )}
          </>
        )}

        {selectedSale && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
            onClick={() => setSelectedSale(null)}>
            <div className="bg-white rounded-lg shadow-xl p-8 max-w-lg w-full" onClick={e => e.stopPropagation()}>
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-xl font-bold">Sale #{selectedSale.saleId}</h2>
                <button onClick={() => setSelectedSale(null)} className="text-gray-400 hover:text-gray-600 text-2xl">×</button>
              </div>
              <p className="text-sm text-gray-600 mb-4">{new Date(selectedSale.createdAt).toLocaleString()}</p>
              <div className="space-y-3 mb-4">
                {selectedSale.items?.map((item, i) => (
                  <div key={i} className="flex justify-between text-sm">
                    <span>{item.productName} × {item.quantity}</span>
                    <span>${Number(item.subtotal).toFixed(2)}</span>
                  </div>
                ))}
              </div>
              <div className="border-t pt-4 flex justify-between font-bold">
                <span>Total</span>
                <span>${Number(selectedSale.totalAmount).toFixed(2)}</span>
              </div>
            </div>
          </div>
        )}
      </div>
      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default SalesHistoryPage;
