import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './features/auth/AuthContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ProtectedRoute from './shared/components/ProtectedRoute';
import Navbar from './shared/components/Navbar';
import Login from './features/auth/LoginPage';
import Register from './features/auth/RegisterPage';
import Dashboard from './features/dashboard/DashboardPage';
import Inventory from './features/product/InventoryPage';
import AddProduct from './features/product/AddProductPage';
import EditProduct from './features/product/EditProductPage';
import SalesForm from './features/sales/SalesFormPage';
import SalesHistory from './features/sales/SalesHistoryPage';
import Profile from './features/auth/ProfilePage';
import './App.css';

function AppContent() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <>
              <Navbar />
              <Dashboard />
            </>
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/inventory"
        element={
          <ProtectedRoute>
            <>
              <Navbar />
              <Inventory />
            </>
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/products/add"
        element={
          <ProtectedRoute>
            <>
              <Navbar />
              <AddProduct />
            </>
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/products/:id/edit"
        element={
          <ProtectedRoute>
            <>
              <Navbar />
              <EditProduct />
            </>
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/sales"
        element={
          <ProtectedRoute>
            <>
              <Navbar />
              <SalesForm />
            </>
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/sales-history"
        element={
          <ProtectedRoute>
            <>
              <Navbar />
              <SalesHistory />
            </>
          </ProtectedRoute>
        }
      />
      
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <>
              <Navbar />
              <Profile />
            </>
          </ProtectedRoute>
        }
      />
      
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
        <ToastContainer position="bottom-right" autoClose={3000} />
      </AuthProvider>
    </Router>
  );
}

export default App;
