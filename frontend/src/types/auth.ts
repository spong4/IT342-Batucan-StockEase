export interface User {
  id: number;
  email: string;
  firstname: string;
  lastname: string;
  role: 'OWNER' | 'STAFF';
}

export interface AuthResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstname: string;
  lastname: string;
  role: 'OWNER' | 'STAFF';
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
  timestamp: string;
}
