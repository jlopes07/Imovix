import { apiFetch } from './api';
import type { Plan, Subscription } from '../types/subscription';

export const SubscriptionService = {
  async getCurrentSubscription(): Promise<Subscription> {
    return await apiFetch<Subscription>('/api/v1/subscriptions/current');
  },

  async listPlans(): Promise<Plan[]> {
    return await apiFetch<Plan[]>('/api/v1/plans');
  },

  async changePlan(planId: string): Promise<Subscription> {
    return await apiFetch<Subscription>('/api/v1/subscriptions/change-plan', {
      method: 'POST',
      body: JSON.stringify({ planId }),
    });
  },
};
