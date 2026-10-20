package br.com.imovix.crm.domain;

/** Estagio do lead no funil de atendimento comercial. */
public enum LeadStatus {
  NEW,
  CONTACTED,
  QUALIFIED,
  VISITING,
  PROPOSAL,
  WON,
  LOST
}
