import api from '../../shared/services/api';
import { ApiResponse, AuthResponse, LoginRequest, RegisterRequest } from './types';

export const authService = {
  register: (data: RegisterRequest) =>
    api.post<ApiResponse<AuthResponse>>('/auth/register', data),
  login: (data: LoginRequest) =>
    api.post<ApiResponse<AuthResponse>>('/auth/login', data),
  getCurrentUser: () =>
    api.get<ApiResponse<any>>('/auth/me'),
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  },
};
