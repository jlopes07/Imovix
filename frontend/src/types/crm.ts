export type ContactType =
  | 'BUYER'
  | 'TENANT_SEEKER'
  | 'OWNER'
  | 'INVESTOR'
  | 'PARTNER'
  | 'OTHER';

export type LeadStatus =
  | 'NEW'
  | 'CONTACTED'
  | 'QUALIFIED'
  | 'VISITING'
  | 'PROPOSAL'
  | 'WON'
  | 'LOST';

export type LeadSource =
  | 'WEBSITE'
  | 'PORTAL'
  | 'WHATSAPP'
  | 'PHONE'
  | 'REFERRAL'
  | 'WALK_IN'
  | 'OTHER';

export type ActivityType =
  | 'VISIT'
  | 'CALL'
  | 'WHATSAPP'
  | 'EMAIL'
  | 'MEETING'
  | 'TASK';

export type ActivityStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';

export interface Lead {
  id: string;
  tenantId: string;
  name: string;
  email: string | null;
  phone: string | null;
  type: ContactType;
  status: LeadStatus;
  source: LeadSource;
  assignedUserId: string | null;
  interestedPropertyId: string | null;
  budgetMin: number | null;
  budgetMax: number | null;
  notes: string | null;
  createdBy: string | null;
  updatedBy: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateLeadPayload {
  name: string;
  email?: string;
  phone?: string;
  type: ContactType;
  status?: LeadStatus;
  source?: LeadSource;
  assignedUserId?: string;
  interestedPropertyId?: string;
  budgetMin?: number;
  budgetMax?: number;
  notes?: string;
}

export interface UpdateLeadPayload {
  name: string;
  email?: string;
  phone?: string;
  type: ContactType;
  source?: LeadSource;
  assignedUserId?: string;
  interestedPropertyId?: string;
  budgetMin?: number;
  budgetMax?: number;
  notes?: string;
}

export interface Activity {
  id: string;
  tenantId: string;
  leadId: string;
  propertyId: string | null;
  userId: string | null;
  type: ActivityType;
  status: ActivityStatus;
  title: string;
  description: string | null;
  scheduledAt: string;
  completedAt: string | null;
  feedback: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ScheduleActivityPayload {
  leadId: string;
  propertyId?: string;
  userId?: string;
  type: ActivityType;
  title: string;
  description?: string;
  scheduledAt: string;
}

export interface CompleteActivityPayload {
  feedback?: string;
  completedAt?: string;
}

export const LEAD_STATUS_LABELS: Record<LeadStatus, string> = {
  NEW: 'Novo Lead',
  CONTACTED: 'Primeiro Contato',
  QUALIFIED: 'Qualificado',
  VISITING: 'Em Visita',
  PROPOSAL: 'Proposta Enviada',
  WON: 'Negocio Fechado',
  LOST: 'Perdido / Arquivado',
};

export const CONTACT_TYPE_LABELS: Record<ContactType, string> = {
  BUYER: 'Comprador',
  TENANT_SEEKER: 'Locatario',
  OWNER: 'Proprietario',
  INVESTOR: 'Investidor',
  PARTNER: 'Parceiro',
  OTHER: 'Outro',
};

export const LEAD_SOURCE_LABELS: Record<LeadSource, string> = {
  WEBSITE: 'Site Oficial',
  PORTAL: 'Portal Imobiliario',
  WHATSAPP: 'WhatsApp',
  PHONE: 'Telefone',
  REFERRAL: 'Indicacao',
  WALK_IN: 'Visita Presencial',
  OTHER: 'Outro Canal',
};

export const ACTIVITY_TYPE_LABELS: Record<ActivityType, string> = {
  VISIT: 'Visita ao Imovel',
  CALL: 'Ligacao Telefonica',
  WHATSAPP: 'Mensagem WhatsApp',
  EMAIL: 'E-mail Comercial',
  MEETING: 'Reuniao Presencial/Online',
  TASK: 'Tarefa Interna',
};
