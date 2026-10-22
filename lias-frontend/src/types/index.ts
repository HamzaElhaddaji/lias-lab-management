// Authentication
export interface AuthResponse {
  token: string;
  refreshToken?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

// Member
export interface Member {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  biography?: string;
  interests?: string;
  establishment?: string;
  originLaboratory?: string;
  photoUrl?: string;
  status: 'PENDING' | 'ACTIVE' | 'FROZEN' | 'INACTIVE';
  createdAt: string;
}

export interface MemberProfile extends Member {
  role?: string;
  affiliation?: Affiliation[];
}

// Affiliation
export interface Affiliation {
  id: number;
  laboratory: string;
  team: string;
  startDate: string;
  endDate?: string;
}

// Event
export interface Event {
  id: number;
  title: string;
  description: string;
  location: string;
  startDate: string;
  endDate: string;
  type: 'SEMINAR' | 'CONFERENCE' | 'WORKSHOP' | 'MEETING' | 'OTHER';
  status: 'PLANNED' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';
  edition?: string;
  website?: string;
  createdBy: number;
  createdAt: string;
}

// Publication
export interface Publication {
  id: number;
  title: string;
  authors: string;
  journal?: string;
  conference?: string;
  doi?: string;
  url?: string;
  abstractText?: string;
  team: string;
  year: number;
  type: 'JOURNAL' | 'CONFERENCE' | 'BOOK' | 'OTHER';
  createdAt: string;
}

// Document
export interface Document {
  id: number;
  fileName: string;
  description?: string;
  type: string;
  fileUrl: string;
  eventId?: number;
  uploadedBy: number;
  uploadedAt: string;
}

// Meeting
export interface Meeting {
  id: number;
  title: string;
  description: string;
  location: string;
  date: string;
  status: 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  processVerbaux?: string;
  createdAt: string;
}

// Equipment
export interface Equipment {
  id: number;
  name: string;
  serialNumber: string;
  quantity: number;
  arrivalDate: string;
  condition: string;
  description?: string;
  notes?: string;
}

export interface EquipmentRequest {
  id: number;
  equipmentName: string;
  equipmentDescription: string;
  quantityRequested: number;
  justification: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  requestedBy: number;
  createdAt: string;
}

// Notification
export interface Notification {
  id: number;
  title: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'SUCCESS' | 'ERROR';
  read: boolean;
  createdAt: string;
}

// Pagination
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// API Response
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  timestamp: string;
}
