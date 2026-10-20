package br.com.imovix.crm.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de persistencia para a entidade {@link Activity} com escopo obrigatorio de tenant. */
public interface ActivityRepository {

  Activity save(Activity activity);

  Optional<Activity> findByTenantIdAndId(UUID tenantId, UUID id);

  List<Activity> findByTenantIdAndLeadId(UUID tenantId, UUID leadId);

  List<Activity> findUpcomingByTenantId(UUID tenantId, Instant from, Instant to);

  void delete(Activity activity);
}
