package br.com.imovix.crm.domain;

import br.com.imovix.tenancy.domain.TenantScoped;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Entidade de dominio representando um Lead ou Contato no CRM da imobiliaria. */
public class Lead implements TenantScoped {

  private final UUID id;
  private final UUID tenantId;
  private String name;
  private String email;
  private String phone;
  private ContactType type;
  private LeadStatus status;
  private LeadSource source;
  private UUID assignedUserId;
  private UUID interestedPropertyId;
  private BigDecimal budgetMin;
  private BigDecimal budgetMax;
  private String notes;
  private UUID createdBy;
  private UUID updatedBy;
  private final Instant createdAt;
  private Instant updatedAt;

  public Lead(
      UUID id,
      UUID tenantId,
      String name,
      String email,
      String phone,
      ContactType type,
      LeadStatus status,
      LeadSource source,
      UUID assignedUserId,
      UUID interestedPropertyId,
      BigDecimal budgetMin,
      BigDecimal budgetMax,
      String notes,
      UUID createdBy,
      UUID updatedBy,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.tenantId = Objects.requireNonNull(tenantId, "O identificador do tenant e obrigatorio.");
    this.name = validateName(name);
    this.email = email != null ? email.trim() : null;
    this.phone = phone != null ? phone.trim() : null;
    this.type = type != null ? type : ContactType.BUYER;
    this.status = status != null ? status : LeadStatus.NEW;
    this.source = source != null ? source : LeadSource.WEBSITE;
    this.assignedUserId = assignedUserId;
    this.interestedPropertyId = interestedPropertyId;
    this.budgetMin = budgetMin;
    this.budgetMax = budgetMax;
    this.notes = notes != null ? notes.trim() : null;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
    Instant now = Instant.now();
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
  }

  private String validateName(String name) {
    if (name == null || name.trim().isBlank()) {
      throw new IllegalArgumentException("O nome do lead e obrigatorio.");
    }
    return name.trim();
  }

  public void update(
      String name,
      String email,
      String phone,
      ContactType type,
      LeadSource source,
      UUID assignedUserId,
      UUID interestedPropertyId,
      BigDecimal budgetMin,
      BigDecimal budgetMax,
      String notes,
      UUID updatedBy) {
    this.name = validateName(name);
    this.email = email != null ? email.trim() : null;
    this.phone = phone != null ? phone.trim() : null;
    this.type = type != null ? type : this.type;
    this.source = source != null ? source : this.source;
    this.assignedUserId = assignedUserId;
    this.interestedPropertyId = interestedPropertyId;
    this.budgetMin = budgetMin;
    this.budgetMax = budgetMax;
    this.notes = notes != null ? notes.trim() : null;
    this.updatedBy = updatedBy;
    this.updatedAt = Instant.now();
  }

  public void changeStatus(LeadStatus newStatus, UUID updatedBy) {
    this.status = Objects.requireNonNull(newStatus, "O novo status e obrigatorio.");
    this.updatedBy = updatedBy;
    this.updatedAt = Instant.now();
  }

  public void assignTo(UUID userId, UUID updatedBy) {
    this.assignedUserId = userId;
    this.updatedBy = updatedBy;
    this.updatedAt = Instant.now();
  }

  public void linkProperty(UUID propertyId, UUID updatedBy) {
    this.interestedPropertyId = propertyId;
    this.updatedBy = updatedBy;
    this.updatedAt = Instant.now();
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getId() {
    return id;
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
