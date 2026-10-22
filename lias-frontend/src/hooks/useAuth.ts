import { useAuthStore } from '@/store/authStore';
import { useState } from 'react';

export const useAuth = () => {
  const { user, token, isAuthenticated } = useAuthStore();
  const [isLoading, setIsLoading] = useState(false);

  return {
    user,
    token,
    isLoading,
    setIsLoading,
    isAuthenticated: isAuthenticated(),
  };
};
