import { useEffect, useState } from 'react';
export function ProfilePhoto({ path }: { path?: string }) {
 const [url, setUrl] = useState('');
 useEffect(() => {
  if (!path) return;
  const controller = new AbortController(); let image = '';
  const base = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '');
  fetch(base + path, { signal: controller.signal, headers: { Authorization: `Bearer ${localStorage.getItem('auth_token') || ''}` } })
   .then(r => { if (!r.ok) throw new Error('Photo unavailable'); return r.blob(); })
   .then(blob => { image = URL.createObjectURL(blob); setUrl(image); }).catch(() => setUrl(''));
  return () => { controller.abort(); if (image) URL.revokeObjectURL(image); };
 }, [path]);
 return url ? <img src={url} alt="Member profile" className="h-24 w-24 rounded-xl border object-cover" /> : null;
}
