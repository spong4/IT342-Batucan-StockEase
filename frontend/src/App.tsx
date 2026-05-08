import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Inventory from './pages/Inventory';
import AddProduct from './pages/AddProduct';
import EditProduct from './pages/EditProduct';
import SalesForm from './pages/SalesForm';
import SalesHistory from './pages/SalesHistory';
import Profile from './pages/Profile';
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
