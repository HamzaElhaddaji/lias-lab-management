import { useAuthStore } from '@/store/authStore';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import {
  BarChart3,
  Users,
  Calendar,
  BookOpen,
  FileText,
  Wrench,
  LogOut,
  Menu,
  ClipboardList,
  Gavel,
  Bell,
} from 'lucide-react';
import { useState } from 'react';
import { cn } from '@/lib/utils';

interface LayoutProps {
  children: React.ReactNode;
}

export default function Layout({ children }: LayoutProps) {
  const navigate = useNavigate();
  const location = useLocation();
  const { logout, user } = useAuthStore();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    { icon: BarChart3, label: 'Dashboard', path: '/dashboard' },
    { icon: Users, label: 'Members', path: '/members' },
    { icon: Calendar, label: 'Events', path: '/events' },
    { icon: BookOpen, label: 'Publications', path: '/publications' },
    { icon: FileText, label: 'Documents', path: '/documents' },
    { icon: Wrench, label: 'Equipment', path: '/equipment' },
    { icon: ClipboardList, label: 'Meetings', path: '/meetings' },
    { icon: Gavel, label: 'Governance', path: '/governance' },
  ];

  return (
    <div className="flex h-screen bg-background text-foreground">
      <aside
        className={`${
          sidebarOpen ? 'w-64' : 'w-20'
        } hidden border-r border-slate-800 bg-slate-950 text-slate-100 transition-all duration-300 md:flex md:flex-col`}
      >
        <div className="border-b border-slate-800 p-5">
          <div className={cn('flex items-center gap-3', !sidebarOpen && 'justify-center')}>
            <div className="flex h-10 w-10 items-center justify-center rounded-md bg-emerald-500 text-sm font-black text-slate-950">
              LI
            </div>
            {sidebarOpen && (
              <div>
                <h1 className="text-base font-bold tracking-wide">LIAS Ops</h1>
                <p className="text-xs text-slate-400">Laboratory console</p>
              </div>
            )}
          </div>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto p-3">
          {menuItems.map((item) => {
            const active = location.pathname === item.path;
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className={cn(
                  'flex w-full items-center gap-3 rounded-md px-3 py-3 text-sm font-medium text-slate-300 transition-colors hover:bg-slate-900 hover:text-white',
                  active && 'bg-emerald-500 text-slate-950 hover:bg-emerald-400 hover:text-slate-950',
                  !sidebarOpen && 'justify-center'
                )}
                title={!sidebarOpen ? item.label : undefined}
              >
                <item.icon className="h-5 w-5 shrink-0" />
                {sidebarOpen && <span>{item.label}</span>}
              </button>
            );
          })}
        </nav>

        <div className="space-y-3 border-t border-slate-800 p-4">
          <div
            className={cn(
              'rounded-md border border-slate-800 bg-slate-900/70 px-3 py-3 text-sm',
              !sidebarOpen && 'flex justify-center'
            )}
          >
            {sidebarOpen && user && (
              <div>
                <p className="truncate font-semibold text-white">
                  {user.firstName} {user.lastName}
                </p>
                <p className="truncate text-xs text-slate-400">
                  {user.email}
                </p>
              </div>
            )}
            {!sidebarOpen && user && (
              <div className="flex h-8 w-8 items-center justify-center rounded-md bg-slate-800 text-xs font-bold">
                {user.firstName?.[0] || 'U'}
              </div>
            )}
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={handleLogout}
            className="w-full justify-center border-slate-700 bg-slate-950 text-slate-100 hover:bg-slate-900"
          >
            <LogOut className="h-4 w-4" />
            {sidebarOpen && <span>Logout</span>}
          </Button>
        </div>
      </aside>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="flex items-center justify-between border-b border-border bg-card px-4 py-3 shadow-sm sm:px-6">
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="hidden rounded-md p-2 transition-colors hover:bg-muted md:inline-flex"
            title="Toggle navigation"
          >
            <Menu className="h-5 w-5" />
          </button>
          <button
            onClick={() => navigate('/dashboard')}
            className="flex items-center gap-2 rounded-md text-sm font-bold md:hidden"
          >
            <span className="flex h-8 w-8 items-center justify-center rounded-md bg-slate-950 text-xs text-emerald-300">
              LI
            </span>
            LIAS Ops
          </button>
          <div className="flex items-center gap-3 text-sm text-muted-foreground">
            <Bell className="h-4 w-4" />
            <span className="hidden sm:inline">Welcome, {user?.firstName || 'member'}</span>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto px-4 py-6 sm:px-6 lg:px-8">{children}</main>
      </div>
    </div>
  );
}
