import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

// TC-FE-001: Login page renders correctly
test('login page renders email and password fields', () => {
  const LoginPage = require('./features/auth/LoginPage').default;
  render(
    <MemoryRouter>
      <LoginPage />
    </MemoryRouter>
  );
  expect(screen.getByPlaceholderText(/email@example.com/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/•••••••/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
});

// TC-FE-002: Register page renders all required fields
test('register page renders all required fields', () => {
  const RegisterPage = require('./features/auth/RegisterPage').default;
  render(
    <MemoryRouter>
      <RegisterPage />
    </MemoryRouter>
  );
  expect(screen.getByPlaceholderText(/john/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/doe/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /register/i })).toBeInTheDocument();
});

// TC-FE-003: Login page has link to register
test('login page contains link to register', () => {
  const LoginPage = require('./features/auth/LoginPage').default;
  render(
    <MemoryRouter>
      <LoginPage />
    </MemoryRouter>
  );
  expect(screen.getByText(/register/i)).toBeInTheDocument();
});

// TC-FE-004: Register page has link to login
test('register page contains link to login', () => {
  const RegisterPage = require('./features/auth/RegisterPage').default;
  render(
    <MemoryRouter>
      <RegisterPage />
    </MemoryRouter>
  );
  expect(screen.getByText(/already have an account/i)).toBeInTheDocument();
});
