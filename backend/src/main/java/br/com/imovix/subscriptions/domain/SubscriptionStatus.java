package br.com.imovix.subscriptions.domain;

/** Status da assinatura de uma empresa cliente no SaaS. */
public enum SubscriptionStatus {
  TRIALING,
  ACTIVE,
  PAST_DUE,
  CANCELED
}
