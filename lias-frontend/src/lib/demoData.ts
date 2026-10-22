import {
  BookOpen,
  CalendarDays,
  FileText,
  FlaskConical,
  ScrollText,
  ShieldCheck,
  Users,
  Wrench,
} from 'lucide-react';

export const memberRows = [
  { name: 'Faouzia Benabbou', email: 'faouzia.benabbou@lias.local', role: 'Director', team: 'ISDIAC', status: 'Active', focus: 'AI, Cloud, NLP' },
  { name: 'Abdessamad Belangour', email: 'abdessamad.belangour@lias.local', role: 'Vice Director', team: 'ILIAS', status: 'Active', focus: 'Software architecture' },
  { name: 'Nawal Sael', email: 'nawal.sael@lias.local', role: 'Team Chief', team: 'ISDIAC', status: 'Active', focus: 'Data science' },
  { name: 'Permanent Demo', email: 'permanent.demo@lias.local', role: 'Permanent Member', team: 'ILIAS', status: 'Active', focus: 'Lab management' },
  { name: 'Doctorant Demo', email: 'doctorant.demo@lias.local', role: 'Doctorant', team: 'ISDIAC', status: 'Limited', focus: 'Doctoral publications' },
];

export const eventRows = [
  { title: 'ICAIS 2025 preparation meeting', type: 'Conference', date: '2026-06-18', owner: 'Direction', status: 'Planned' },
  { title: 'Smart Cities research seminar', type: 'Seminar', date: '2026-06-24', owner: 'SIMA', status: 'Open' },
  { title: 'AI for Healthcare workshop', type: 'Workshop', date: '2026-07-03', owner: 'ISDIAC', status: 'Draft' },
  { title: 'Doctoral progress day', type: 'Internal event', date: '2026-07-11', owner: 'LIAS', status: 'Planned' },
];

export const publicationRows = [
  { title: 'Graph neural models for urban mobility', authors: 'F. Benabbou, N. Sael', year: '2026', team: 'ISDIAC', type: 'Journal' },
  { title: 'Secure IoT architecture for smart campuses', authors: 'S. Ouahabi, A. Belangour', year: '2025', team: 'SDTIC', type: 'Conference' },
  { title: 'Software quality metrics in public platforms', authors: 'A. Ettaoufik, Permanent Demo', year: '2025', team: 'ILIAS', type: 'Journal' },
  { title: 'Hybrid optimization for decision support', authors: 'M. Ait Daoud', year: '2024', team: 'SIMA', type: 'Book chapter' },
];

export const documentRows = [
  { filename: 'PV-direction-2026-06.pdf', type: 'PV', version: 'v3', owner: 'Direction', status: 'Archived' },
  { filename: 'ICAIS-2025-programme.pdf', type: 'Program', version: 'v2', owner: 'Events', status: 'Published' },
  { filename: 'Convention-UH2C-partner.docx', type: 'Convention', version: 'v1', owner: 'Partnerships', status: 'Review' },
  { filename: 'Annual-report-2025.pdf', type: 'Report', version: 'v1', owner: 'Admin', status: 'Ready' },
];

export const equipmentRows = [
  { name: 'Research laptop', quantity: '6', assigned: '4', owner: 'ILIAS', status: 'Available' },
  { name: 'IoT sensor kit', quantity: '12', assigned: '9', owner: 'SDTIC', status: 'Available' },
  { name: 'GPU workstation', quantity: '2', assigned: '2', owner: 'ISDIAC', status: 'Allocated' },
  { name: 'Projector kit', quantity: '3', assigned: '1', owner: 'Meetings', status: 'Available' },
];

export const meetingRows = [
  { title: 'Laboratory council', date: '2026-06-20', agenda: 'Mandates, annual report, equipment', pv: 'Draft' },
  { title: 'ISDIAC weekly sync', date: '2026-06-21', agenda: 'Publications and doctoral follow-up', pv: 'Pending' },
  { title: 'Convention review board', date: '2026-06-27', agenda: 'Partners and agreement history', pv: 'Archived' },
];

export const mandateRows = [
  { title: 'Director mandate', holder: 'Faouzia Benabbou', period: '2025 - Present', status: 'Active' },
  { title: 'Vice-director mandate', holder: 'Abdessamad Belangour', period: '2025 - Present', status: 'Active' },
  { title: 'ISDIAC chief', holder: 'Nawal Sael', period: '2025 - Present', status: 'Active' },
  { title: 'SIMA chief', holder: 'Mohammed Ait Daoud', period: '2025 - Present', status: 'Active' },
];

export const dashboardCards = [
  { label: 'Active members', value: '19', detail: 'Permanent, associate and doctoral profiles', icon: Users, tone: 'emerald' },
  { label: 'Events tracked', value: '8', detail: 'Conferences, workshops and internal sessions', icon: CalendarDays, tone: 'amber' },
  { label: 'Documents archived', value: '34', detail: 'PV, reports, programs and conventions', icon: FileText, tone: 'indigo' },
  { label: 'Equipment requests', value: '5', detail: '2 pending validation this month', icon: Wrench, tone: 'rose' },
];

export const activityFeed = [
  { icon: ShieldCheck, title: 'Mandate validation updated', text: 'Director and vice-director records are active for the current cycle.' },
  { icon: FlaskConical, title: 'Equipment distribution checked', text: 'IoT kits and laptop assignments are balanced by team.' },
  { icon: BookOpen, title: 'Publication review', text: 'Four 2025-2026 publications are ready for annual report export.' },
  { icon: ScrollText, title: 'PV archive', text: 'Latest laboratory council PV is attached to the meeting record.' },
];
