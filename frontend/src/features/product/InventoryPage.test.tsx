import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

// Mock api and AuthContext
jest.mock('../../shared/services/api', () => ({
  get: jest.fn(),
  post: jest.fn(),
  delete: jest.fn(),
  interceptors: { request: { use: jest.fn() }, response: { use: jest.fn() } },
}));

jest.mock('../auth/AuthContext', () => ({
  useAuth: () => ({
    user: { id: 1, email: 'owner@example.com', firstname: 'John', lastname: 'Doe', role: 'OWNER' },
    isAuthenticated: true,
    isLoading: false,
  }),
}));

// TC-FE-008: Inventory page renders Add Product button for OWNER
test('inventory page renders Add Product button for OWNER role', async () => {
  const api = require('../../shared/services/api');
  api.get.mockResolvedValue({
    data: { data: { content: [], totalPages: 0 } },
  });

  const { InventoryPage } = require('./InventoryPage');
  render(
    <MemoryRouter>
      <InventoryPage />
    </MemoryRouter>
  );

  expect(screen.getByText(/\+ add product/i)).toBeInTheDocument();
});

// TC-FE-009: Inventory page renders table headers
test('inventory page renders product table headers', async () => {
  const api = require('../../shared/services/api');
  api.get.mockResolvedValue({
    data: { data: { content: [], totalPages: 0 } },
  });

  const { InventoryPage } = require('./InventoryPage');
  render(
    <MemoryRouter>
      <InventoryPage />
    </MemoryRouter>
  );

  expect(screen.getByText(/product/i)).toBeInTheDocument();
  expect(screen.getByText(/category/i)).toBeInTheDocument();
  expect(screen.getByText(/price/i)).toBeInTheDocument();
  expect(screen.getByText(/quantity/i)).toBeInTheDocument();
});

// TC-FE-010: Inventory page shows search input
test('inventory page renders search input', () => {
  const api = require('../../shared/services/api');
  api.get.mockResolvedValue({
    data: { data: { content: [], totalPages: 0 } },
  });

  const { InventoryPage } = require('./InventoryPage');
  render(
    <MemoryRouter>
      <InventoryPage />
    </MemoryRouter>
  );

  expect(screen.getByPlaceholderText(/search products/i)).toBeInTheDocument();
});
