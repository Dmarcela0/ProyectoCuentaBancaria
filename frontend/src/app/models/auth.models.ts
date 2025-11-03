export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  childName: string;
  age: number;
  guardianName: string;
  guardianEmail: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  childName: string;
  age: number;
}
