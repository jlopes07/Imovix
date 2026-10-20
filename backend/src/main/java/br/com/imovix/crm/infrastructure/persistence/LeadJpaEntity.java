package br.com.imovix.crm.infrastructure.persistence;

import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadSource;
import br.com.imovix.crm.domain.LeadStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA mapeada para a tabela imovix.contacts_leads. */
@Entity
@Table(name = "contacts_leads", schema = "imovix")
public class LeadJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ContactType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private LeadStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "source", nullable = false)
  private LeadSource source;

  @Column(name = "assigned_user_id")
  private UUID assignedUserId;

  @Column(name = "interested_property_id")
  private UUID interestedPropertyId;

  @Column(name = "budget_min")
  private BigDecimal budgetMin;

  @Column(name = "budget_max")
  private BigDecimal budgetMax;

  @Column(name = "notes")
  private String notes;

  @Column(name = "created_by")
  private UUID createdBy;

  @Column(name = "updated_by")
  private UUID updatedBy;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public LeadJpaEntity() {}

  public static LeadJpaEntity fromDomain(Lead lead) {
    LeadJpaEntity entity = new LeadJpaEntity();
    entity.id = lead.getId();
    entity.tenantId = lead.getTenantId();
    entity.name = lead.getName();
    entity.email = lead.getEmail();
    entity.phone = lead.getPhone();
    entity.type = lead.getType();
    entity.status = lead.getStatus();
    entity.source = lead.getSource();
    entity.assignedUserId = lead.getAssignedUserId();
    entity.interestedPropertyId = lead.getInterestedPropertyId();
    entity.budgetMin = lead.getBudgetMin();
    entity.budgetMax = lead.getBudgetMax();
    entity.notes = lead.getNotes();
    entity.createdBy = lead.getCreatedBy();
    entity.updatedBy = lead.getUpdatedBy();
    entity.createdAt = lead.getCreatedAt();
    entity.updatedAt = lead.getUpdatedAt();
    return entity;
  }

  public Lead toDomain() {
    return new Lead(
        this.id,
        this.tenantId,
        this.name,
        this.email,
        this.phone,
        this.type,
        this.status,
        this.source,
        this.assignedUserId,
        this.interestedPropertyId,
        this.budgetMin,
        this.budgetMax,
        this.notes,
        this.createdBy,
        this.updatedBy,
        this.createdAt,
        this.updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public ContactType getType() {
    return type;
  }

  public LeadStatus getStatus() {
    return status;
  }

  public LeadSource getSource() {
    return source;
  }

  public UUID getAssignedUserId() {
    return assignedUserId;
  }

  public UUID getInterestedPropertyId() {
    return interestedPropertyId;
  }

  public BigDecimal getBudgetMin() {
    return budgetMin;
  }

  public BigDecimal getBudgetMax() {
    return budgetMax;
  }

  public String getNotes() {
    return notes;
  }

  public UUID getCreatedBy() {
    return createdBy;
  }

  public UUID getUpdatedBy() {
    return updatedBy;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
