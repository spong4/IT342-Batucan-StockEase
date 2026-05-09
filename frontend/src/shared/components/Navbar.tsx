import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../features/auth/AuthContext';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-blue-600 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          <Link to="/dashboard" className="text-2xl font-bold">
            StockEase
          </Link>

          <div className="hidden md:flex items-center space-x-6">
            <Link to="/dashboard" className="hover:text-blue-200">Dashboard</Link>
            <Link to="/inventory" className="hover:text-blue-200">Inventory</Link>
            {user?.role === 'OWNER' && (
              <Link to="/products/add" className="hover:text-blue-200">+ Add Product</Link>
            )}
            <Link to="/sales" className="hover:text-blue-200">Record Sale</Link>
            <Link to="/sales-history" className="hover:text-blue-200">Sales History</Link>

            <div className="relative group">
              <button className="hover:text-blue-200 flex items-center space-x-2">
                <span>{user?.firstname}</span>
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
                </svg>
              </button>
              <div className="absolute right-0 w-48 bg-white text-gray-900 rounded shadow-lg hidden group-hover:block z-50">
                <Link to="/profile" className="block px-4 py-2 hover:bg-blue-50 first:rounded-t">Profile</Link>
                <button onClick={handleLogout} className="w-full text-left px-4 py-2 hover:bg-red-50 text-red-600 last:rounded-b">Logout</button>
              </div>
            </div>
          </div>

          <button
            className="md:hidden p-2"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
        </div>

        {mobileMenuOpen && (
          <div className="md:hidden pb-4 space-y-2">
            <Link to="/dashboard" className="block py-2 hover:text-blue-200">Dashboard</Link>
            <Link to="/inventory" className="block py-2 hover:text-blue-200">Inventory</Link>
            {user?.role === 'OWNER' && (
              <Link to="/products/add" className="block py-2 hover:text-blue-200">+ Add Product</Link>
            )}
            <Link to="/sales" className="block py-2 hover:text-blue-200">Record Sale</Link>
            <Link to="/sales-history" className="block py-2 hover:text-blue-200">Sales History</Link>
            <Link to="/profile" className="block py-2 hover:text-blue-200">Profile</Link>
            <button onClick={handleLogout} className="block py-2 text-red-300 hover:text-red-100">Logout</button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
