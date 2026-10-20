package br.com.imovix.properties.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.properties.application.CreatePropertyCommand;
import br.com.imovix.properties.application.CreatePropertyUseCase;
import br.com.imovix.properties.application.DeletePropertyUseCase;
import br.com.imovix.properties.application.GetPropertyUseCase;
import br.com.imovix.properties.application.ListPropertiesUseCase;
import br.com.imovix.properties.application.PagedPropertiesResponse;
import br.com.imovix.properties.application.PropertyResponse;
import br.com.imovix.properties.application.UpdatePropertyCommand;
import br.com.imovix.properties.application.UpdatePropertyUseCase;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyStatus;
import br.com.imovix.properties.domain.PropertyType;
import br.com.imovix.security.CorsProperties;
import br.com.imovix.shared.error.GlobalExceptionHandler;
import br.com.imovix.tenancy.application.TenantContext;
import br.com.imovix.tenancy.domain.TenantId;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PropertyController.class)
@Import(GlobalExceptionHandler.class)
@EnableConfigurationProperties(CorsProperties.class)
@DisplayName("Testes WebMvc: PropertyController")
class PropertyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CreatePropertyUseCase createPropertyUseCase;
  @MockitoBean private GetPropertyUseCase getPropertyUseCase;
  @MockitoBean private ListPropertiesUseCase listPropertiesUseCase;
  @MockitoBean private UpdatePropertyUseCase updatePropertyUseCase;
  @MockitoBean private DeletePropertyUseCase deletePropertyUseCase;

  private UUID currentTenantId;

  @BeforeEach
  void setUp() {
    currentTenantId = UUID.randomUUID();
    TenantContext.set(TenantId.from(currentTenantId));
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  @WithMockUser
  @DisplayName("POST /api/v1/properties deve criar imóvel e retornar 201 Created com Location")
  void shouldCreatePropertySuccessfully() throws Exception {
    UUID propertyId = UUID.randomUUID();
    PropertyResponse response =
        new PropertyResponse(
            propertyId,
            currentTenantId,
            "AP-101",
            "Apto Reformado",
            null,
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            PropertyStatus.AVAILABLE,
            null,
            null,
            null,
            null,
            null,
            Instant.now(),
            Instant.now());

    when(createPropertyUseCase.execute(any(CreatePropertyCommand.class))).thenReturn(response);

    String payload =
        """
        {
          "code": "AP-101",
          "title": "Apto Reformado",
          "type": "APARTMENT",
          "purpose": "SALE",
          "salePrice": 500000.00
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/properties")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").value(propertyId.toString()))
        .andExpect(jsonPath("$.code").value("AP-101"))
        .andExpect(jsonPath("$.tenantId").value(currentTenantId.toString()));
  }

  @Test
  @WithMockUser
  @DisplayName("POST /api/v1/properties com campos inválidos deve retornar 400 Problem Details")
  void shouldReturn400WhenInvalidFields() throws Exception {
    String invalidPayload =
        """
        {
          "code": "",
          "title": "",
          "salePrice": -50.00
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/properties")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Erro de Validação"))
        .andExpect(jsonPath("$.invalidParams.title").exists());
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/properties deve retornar lista paginada do tenant atual")
  void shouldListProperties() throws Exception {
    PagedPropertiesResponse response = new PagedPropertiesResponse(List.of(), 0, 20, 0L, 0);

    when(listPropertiesUseCase.execute(eq(currentTenantId), eq(0), eq(20))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/properties?page=0&size=20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/properties/{id} deve retornar imóvel do tenant")
  void shouldGetPropertyById() throws Exception {
    UUID propertyId = UUID.randomUUID();
    PropertyResponse response =
        new PropertyResponse(
            propertyId,
            currentTenantId,
            "AP-101",
            "Apto Reformado",
            null,
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            PropertyStatus.AVAILABLE,
            null,
            null,
            null,
            null,
            null,
            Instant.now(),
            Instant.now());

    when(getPropertyUseCase.execute(currentTenantId, propertyId)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/properties/" + propertyId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(propertyId.toString()))
        .andExpect(jsonPath("$.code").value("AP-101"));
  }

  @Test
  @WithMockUser
  @DisplayName("PUT /api/v1/properties/{id} deve atualizar imóvel e retornar 200 OK")
  void shouldUpdateProperty() throws Exception {
    UUID propertyId = UUID.randomUUID();
    PropertyResponse response =
        new PropertyResponse(
            propertyId,
            currentTenantId,
            "AP-101",
            "Apto Reformado Atualizado",
            null,
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            PropertyStatus.AVAILABLE,
            null,
            null,
            null,
            null,
            null,
            Instant.now(),
            Instant.now());

    when(updatePropertyUseCase.execute(any(UpdatePropertyCommand.class))).thenReturn(response);

    String payload =
        """
        {
          "code": "AP-101",
          "title": "Apto Reformado Atualizado",
          "type": "APARTMENT",
          "purpose": "SALE"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/properties/" + propertyId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Apto Reformado Atualizado"));
  }

  @Test
  @WithMockUser
  @DisplayName("DELETE /api/v1/properties/{id} deve desativar imóvel e retornar 204 No Content")
  void shouldDeleteProperty() throws Exception {
    UUID propertyId = UUID.randomUUID();

    mockMvc
        .perform(delete("/api/v1/properties/" + propertyId).with(csrf()))
        .andExpect(status().isNoContent());

    verify(deletePropertyUseCase).execute(eq(currentTenantId), eq(propertyId), any());
  }
}
