import { BrowserRouter, Routes, Route, Navigate, Link, useLocation } from 'react-router-dom';
import { useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '@/store/authStore';
import LoginPage from '@/pages/auth/LoginPage';
import { PublicHome, Apply } from '@/pages/PublicPages';
import { Events, Publications, Documents, Equipment, Meetings, Governance, Collaborations } from '@/pages/LiveModules';
import { Dashboard, Members, Profile, Admissions, Notifications, Calendar, Discussions, Reports, Search, History, Affiliations, Archive } from '@/pages/WorkspacePages';
import { useProfile, has, ErrorMessage } from '@/components/ResourcePage';
import { Button } from '@/components/ui/Button';
const routes = [
 { path: '/dashboard', label: 'Overview', component: Dashboard, allowed: [] },
 { path: '/profile', label: 'My profile', component: Profile, allowed: [] },
 { path: '/publications', label: 'Publications', component: Publications, allowed: [] },
 { path: '/events', label: 'Events', component: Events, allowed: ['MEMBER', 'ASSOCIATE', 'ADMIN', 'DIRECTOR'] },
 { path: '/calendar', label: 'Calendar', component: Calendar, allowed: ['MEMBER', 'ASSOCIATE', 'ADMIN', 'DIRECTOR'] },
 { path: '/members', label: 'Members', component: Members, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/affiliations', label: 'Affiliations', component: Affiliations, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/documents', label: 'Documents', component: Documents, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/equipment', label: 'Equipment', component: Equipment, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/meetings', label: 'Meetings & minutes', component: Meetings, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/governance', label: 'Governance', component: Governance, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/collaborations', label: 'Collaborations', component: Collaborations, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/discussions', label: 'Discussions', component: Discussions, allowed: ['MEMBER', 'ADMIN', 'DIRECTOR'] },
 { path: '/admissions', label: 'Applications', component: Admissions, allowed: ['DIRECTOR'] },
 { path: '/reports', label: 'Activity reports', component: Reports, allowed: ['ADMIN', 'DIRECTOR'] },
 { path: '/archive', label: 'Archive', component: Archive, allowed: ['ADMIN', 'DIRECTOR'] },
 { path: '/history', label: 'Revision history', component: History, allowed: ['ADMIN', 'DIRECTOR'] },
 { path: '/search', label: 'Global search', component: Search, allowed: [] },
 { path: '/notifications', label: 'Notifications', component: Notifications, allowed: [] },
];
function Workspace() {
 const token = useAuthStore(s => s.token); const logout = useAuthStore(s => s.logout); const client = useQueryClient(); const location = useLocation(); const [open, setOpen] = useState(false); const { data: profile, isLoading, error } = useProfile();
 if (!token) return <Navigate to="/login" replace />;
 if (isLoading) return <div className="p-10">Loading your workspace…</div>;
 if (error) return <div className="space-y-4 p-10"><ErrorMessage error={error} /><Button onClick={() => { logout(); client.clear(); }}>Return to login</Button></div>;
 const roles = (profile?.roles || []) as string[]; const available = routes.filter(r => !r.allowed.length || has(roles, ...r.allowed));
 return <div className="min-h-screen bg-slate-50"><header className="sticky top-0 z-20 flex items-center justify-between border-b bg-white px-5 py-4 lg:ml-64"><button onClick={() => setOpen(!open)} className="font-bold lg:hidden" aria-label="Toggle navigation">☰ LIAS</button><p className="hidden text-sm text-slate-500 lg:block">LIAS · Laboratory workspace</p><div className="flex items-center gap-4 text-sm"><Link to="/notifications" className="font-semibold text-emerald-700">Notifications</Link><Link to="/profile">{profile?.firstName} {profile?.lastName}</Link></div></header><aside className={`${open ? 'flex' : 'hidden'} fixed bottom-0 left-0 top-0 z-30 w-64 flex-col bg-slate-950 text-slate-200 lg:flex`}><Link to="/" className="border-b border-slate-800 p-6"><span className="text-2xl font-black text-emerald-400">LIAS</span><span className="mt-1 block text-xs text-slate-400">Research. Collaboration. Memory.</span></Link><nav className="flex-1 overflow-y-auto px-3 py-4">{available.map(r => <Link key={r.path} to={r.path} onClick={() => setOpen(false)} className={`mb-1 block rounded-lg px-4 py-2 text-sm ${location.pathname === r.path ? 'bg-emerald-500 font-bold text-slate-950' : 'hover:bg-slate-800'}`}>{r.label}</Link>)}</nav><div className="border-t border-slate-800 p-4"><p className="mb-3 truncate text-xs text-slate-400">{profile?.email} · {roles.join(', ')}</p><button className="w-full rounded-lg border border-slate-700 p-2 text-sm" onClick={() => { logout(); client.clear(); }}>Sign out</button>{open && <button onClick={() => setOpen(false)} className="mt-3 w-full text-sm lg:hidden">Close menu</button>}</div></aside><main className="p-5 lg:ml-64 lg:p-8"><Routes>{available.map(r => <Route key={r.path} path={r.path} element={<r.component />} />)}<Route path="*" element={<Navigate to="/dashboard" replace />} /></Routes></main></div>;
}
export default function App() { return <BrowserRouter><Routes><Route path="/" element={<PublicHome />} /><Route path="/login" element={<LoginPage />} /><Route path="/register" element={<Apply />} /><Route path="*" element={<Workspace />} /></Routes></BrowserRouter>; }
