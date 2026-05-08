import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

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
          {/* Logo */}
          <Link to="/dashboard" className="text-2xl font-bold">
            StockEase
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-6">
            <Link to="/dashboard" className="hover:text-blue-200">
              Dashboard
            </Link>
            <Link to="/inventory" className="hover:text-blue-200">
              Inventory
            </Link>
            {user?.role === 'OWNER' && (
              <Link to="/products/add" className="hover:text-blue-200">
                + Add Product
              </Link>
            )}
            <Link to="/sales" className="hover:text-blue-200">
              Record Sale
            </Link>
            <Link to="/sales-history" className="hover:text-blue-200">
              Sales History
            </Link>

            {/* User Dropdown */}
            <div className="relative group">
              <button className="hover:text-blue-200 flex items-center space-x-2">
                <span>{user?.firstname}</span>
                <svg
                  className="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M19 14l-7 7m0 0l-7-7m7 7V3"
                  />
                </svg>
              </button>

              <div className="absolute right-0 w-48 bg-white text-gray-900 rounded shadow-lg hidden group-hover:block z-50">
                <Link
                  to="/profile"
                  className="block px-4 py-2 hover:bg-blue-50 first:rounded-t"
                >
                  Profile
                </Link>
                <button
                  onClick={handleLogout}
                  className="w-full text-left px-4 py-2 hover:bg-red-50 text-red-600 last:rounded-b"
                >
                  Logout
                </button>
              </div>
            </div>
          </div>

          {/* Mobile Menu Button */}
          <button
            className="md:hidden"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            <svg
              className="w-6 h-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 6h16M4 12h16M4 18h16"
              />
            </svg>
          </button>
        </div>

        {/* Mobile Navigation */}
        {mobileMenuOpen && (
          <div className="md:hidden pb-4 space-y-2">
            <Link
              to="/dashboard"
              className="block px-4 py-2 hover:bg-blue-700 rounded"
            >
              Dashboard
            </Link>
            <Link
              to="/inventory"
              className="block px-4 py-2 hover:bg-blue-700 rounded"
            >
              Inventory
            </Link>
            {user?.role === 'OWNER' && (
              <Link
                to="/products/add"
                className="block px-4 py-2 hover:bg-blue-700 rounded"
              >
                + Add Product
              </Link>
            )}
            <Link
              to="/sales"
              className="block px-4 py-2 hover:bg-blue-700 rounded"
            >
              Record Sale
            </Link>
            <Link
              to="/sales-history"
              className="block px-4 py-2 hover:bg-blue-700 rounded"
            >
              Sales History
            </Link>
            <Link
              to="/profile"
              className="block px-4 py-2 hover:bg-blue-700 rounded"
            >
              Profile
            </Link>
            <button
              onClick={handleLogout}
              className="w-full text-left px-4 py-2 hover:bg-red-700 rounded text-red-200"
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
