export interface Plan {
  id: string;
  code: string;
  name: string;
  description: string;
  price: number;
  billingCycle: string;
  maxProperties: number;
  maxUsers: number;
  active: boolean;
}

export interface Subscription {
  id: string;
  tenantId: string;
  plan: Plan;
  status: 'TRIALING' | 'ACTIVE' | 'PAST_DUE' | 'CANCELED';
  currentPeriodStart: string;
  currentPeriodEnd: string;
  cancelAtPeriodEnd: boolean;
  isActive: boolean;
  currentPropertiesCount: number;
  maxProperties: number;
  isPropertiesLimitReached: boolean;
  currentUsersCount: number;
  maxUsers: number;
  isUsersLimitReached: boolean;
}

export interface ChangePlanPayload {
  planId: string;
}
