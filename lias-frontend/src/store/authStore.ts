import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { Member } from '@/types';

interface AuthStore {
  user: Member | null;
  token: string | null;
  isLoading: boolean;
  setUser: (user: Member | null) => void;
  setToken: (token: string) => void;
  logout: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      user: null,
      token: localStorage.getItem('auth_token'),
      isLoading: false,
      setUser: (user) => set({ user }),
      setToken: (token) => {
        localStorage.setItem('auth_token', token);
        set({ token });
      },
      logout: () => {
        localStorage.removeItem('auth_token');
        set({ user: null, token: null });
      },
      isAuthenticated: () => {
        return get().token !== null && get().user !== null;
      },
    }),
    {
      name: 'auth-store',
      partialize: (state) => ({ token: state.token, user: state.user }),
    }
  )
);
