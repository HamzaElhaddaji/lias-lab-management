import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/Card';
import { request } from '@/services/http';
import { useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '@/store/authStore';
import { Building2, LockKeyhole } from 'lucide-react';

export default function LoginPage() {
  const navigate = useNavigate();
  const client = useQueryClient();
  const { setToken, setUser } = useAuthStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await request('/api/auth/login', 'POST', { email, password });
      client.clear();
      setToken(response.token);
      const profile = await request('/api/members/me');
      setUser(profile);
      client.setQueryData(['profile'], profile);

      navigate('/dashboard');
    } catch (err: unknown) {
      useAuthStore.getState().logout();
      setError(err instanceof Error ? err.message : 'Login failed.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-background p-4">
      <div className="grid w-full max-w-5xl overflow-hidden rounded-lg border border-border bg-card shadow-xl lg:grid-cols-[0.9fr_1.1fr]">
        <section className="hidden bg-slate-950 p-10 text-slate-100 lg:block">
          <div className="flex h-full flex-col justify-between">
            <div>
              <div className="flex h-12 w-12 items-center justify-center rounded-md bg-emerald-500 text-sm font-black text-slate-950">
                LI
              </div>
              <h1 className="mt-8 text-3xl font-bold tracking-tight">LIAS Ops</h1>
              <p className="mt-3 max-w-sm text-sm leading-6 text-slate-400">
                Laboratory administration workspace for members, events, equipment and governance.
              </p>
            </div>
            <div className="grid gap-3 text-sm text-slate-300">
              <div className="flex items-center gap-2">
                <Building2 className="h-4 w-4 text-emerald-300" />
                LIAS laboratory management
              </div>
              <div className="flex items-center gap-2">
                <LockKeyhole className="h-4 w-4 text-emerald-300" />
                Secured member access
              </div>
            </div>
          </div>
        </section>

        <Card className="border-0 shadow-none">
          <CardHeader className="space-y-2 p-8">
            <CardTitle className="text-2xl">Sign in</CardTitle>
            <CardDescription>Access the LIAS administration console.</CardDescription>
          </CardHeader>
          <CardContent className="p-8 pt-0">
          <form onSubmit={handleLogin} className="space-y-4">
            {error && (
              <div className="rounded-md bg-destructive/10 p-3 text-sm text-destructive">
                {error}
              </div>
            )}

            <div className="space-y-2">
              <label className="text-sm font-medium">Email</label>
              <Input
                type="email"
                placeholder="member@lias.ma"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={isLoading}
                required
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Password</label>
              <Input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isLoading}
                required
              />
            </div>

            <Button
              type="submit"
              className="w-full"
              disabled={isLoading}
            >
              {isLoading ? 'Signing in...' : 'Sign in'}
            </Button>

            <p className="text-center text-sm text-muted-foreground">
              Need an account?{' '}
              <button
                type="button"
                onClick={() => navigate('/register')}
                className="text-primary hover:underline font-medium"
              >
                Register
              </button>
            </p>
          </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
