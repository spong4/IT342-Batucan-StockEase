import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from './AuthContext';

// Mock api module
jest.mock('../../shared/services/api', () => ({
  post: jest.fn(),
  get: jest.fn(),
  delete: jest.fn(),
  interceptors: {
    request: { use: jest.fn() },
    response: { use: jest.fn() },
  },
  defaults: { headers: { common: {} } },
}));

const renderWithProviders = (ui: React.ReactElement) =>
  render(
    <AuthProvider>
      <MemoryRouter>{ui}</MemoryRouter>
    </AuthProvider>
  );

// TC-FE-005: AuthContext provides user as null by default
test('AuthContext initial state has no user', () => {
  const TestComponent = () => {
    const { useAuth } = require('./AuthContext');
    const { user, isAuthenticated } = useAuth();
    return (
      <div>
        <span data-testid="auth-status">{isAuthenticated ? 'logged-in' : 'logged-out'}</span>
      </div>
    );
  };
  renderWithProviders(<TestComponent />);
  expect(screen.getByTestId('auth-status')).toHaveTextContent('logged-out');
});

// TC-FE-006: Login form validates empty fields
test('login form shows validation error for empty submission', async () => {
  const LoginPage = require('./LoginPage').default;
  renderWithProviders(<LoginPage />);

  const submitButton = screen.getByRole('button', { name: /login/i });
  fireEvent.click(submitButton);

  // HTML5 required validation prevents empty submit
  const emailInput = screen.getByPlaceholderText(/email@example.com/i);
  expect(emailInput).toBeRequired();
});

// TC-FE-007: Register form validates password match
test('register form detects password mismatch', async () => {
  const api = require('../../shared/services/api');
  api.post.mockRejectedValue({ response: { data: { error: { message: 'Test error' } } } });

  const RegisterPage = require('./RegisterPage').default;
  renderWithProviders(<RegisterPage />);

  fireEvent.change(screen.getByPlaceholderText(/john/i), { target: { value: 'Jane' } });
  fireEvent.change(screen.getByPlaceholderText(/doe/i), { target: { value: 'Smith' } });
  fireEvent.change(screen.getByPlaceholderText(/email@example.com/i), { target: { value: 'test@example.com' } });

  const passwordInputs = screen.getAllByPlaceholderText(/•••••••/i);
  fireEvent.change(passwordInputs[0], { target: { value: 'password123' } });
  fireEvent.change(passwordInputs[1], { target: { value: 'different123' } });

  fireEvent.click(screen.getByRole('button', { name: /register/i }));

  await waitFor(() => {
    expect(screen.getByText(/passwords do not match/i)).toBeInTheDocument();
  });
});
