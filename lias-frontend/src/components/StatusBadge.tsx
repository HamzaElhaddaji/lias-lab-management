import { cn } from '@/lib/utils';

type StatusTone = 'green' | 'amber' | 'blue' | 'rose' | 'slate';

const toneClasses: Record<StatusTone, string> = {
  green: 'border-emerald-200 bg-emerald-50 text-emerald-700',
  amber: 'border-amber-200 bg-amber-50 text-amber-700',
  blue: 'border-indigo-200 bg-indigo-50 text-indigo-700',
  rose: 'border-rose-200 bg-rose-50 text-rose-700',
  slate: 'border-slate-200 bg-slate-50 text-slate-700',
};

const statusTone: Record<string, StatusTone> = {
  active: 'green',
  available: 'green',
  archived: 'green',
  ready: 'green',
  published: 'green',
  open: 'blue',
  planned: 'blue',
  pending: 'amber',
  draft: 'amber',
  review: 'amber',
  limited: 'amber',
  allocated: 'rose',
};

export function StatusBadge({ value }: { value: string }) {
  const tone = statusTone[value.toLowerCase()] || 'slate';

  return (
    <span
      className={cn(
        'inline-flex h-7 items-center rounded-md border px-2.5 text-xs font-semibold',
        toneClasses[tone]
      )}
    >
      {value}
    </span>
  );
}
