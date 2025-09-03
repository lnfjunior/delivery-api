
# Delivery API (Spring Boot + Gradle)

API para gerenciamento de **clientes**, **produtos** e **pedidos de entrega**, cobrindo os requisitos do desafio. 
Tecnologias: Java 17, Spring Boot 3.3, JPA, Validation, Redis (cache), OpenAPI (Swagger), OAuth2 Resource Server (Keycloak).

## Arquitetura (resumo)
- **Camadas**: controller → service → repository → domain
- **Entidades**: Customer, Product, PurchaseOrder, OrderItem, OrderStatus
- **Banco**: H2 (profile default) e PostgreSQL (profile `docker` via `application-docker.yml`)
- **Cache**: Redis com TTL 10 min (listas e get por id de clientes/produtos)
- **Auth**: Keycloak (JWT) como Resource Server (todas as rotas da API exigem autenticação, Swagger liberado)
- **Documentação**: Springdoc UI em `/swagger-ui.html`

## Como executar (opção 1: Docker Compose - Postgres/Redis/Keycloak)
> Requer Docker + Docker Compose.

```bash
docker compose up -d
# Ele vai subir toda a estrutura necessária, inclusive a API delivery-api-app na porta 8080 e o keycloak na porta 8081
```

### Variáveis importantes
- `KEYCLOAK_ISSUER_URI=http://localhost:8081/realms/delivery-realm` (default já cobre isto)
- `SPRING_PROFILES_ACTIVE=docker` (para usar Postgres/Redis do compose)

## Como executar (opção 2: sem Docker, usando H2 e Redis local opcional)
- **H2** já vem configurado no `application.yml` (memória). 
- Redis é opcional (mas recomendado para avaliação)

```bash
# Sem Docker, com H2
gradle bootRun
```

### Acessos
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Keycloak Admin**: http://localhost:8081 (admin/admin)
  - Realm: `delivery-realm`
  - Usuários:
    - `user` / `user123` (role: `delivery_user`)
    - `admin` / `admin123` (roles: `delivery_admin`, `delivery_user`)
  - Client: `delivery-api` (public, Direct Access Grants habilitado)

## Obtendo token (Direct Access Grants)
```bash
# Usuário comum
curl -X POST "http://localhost:8081/realms/delivery-realm/protocol/openid-connect/token"       -H "Content-Type: application/x-www-form-urlencoded"       -d "grant_type=password&client_id=delivery-api&username=user&password=user123"

# Admin
curl -X POST "http://localhost:8081/realms/delivery-realm/protocol/openid-connect/token"       -H "Content-Type: application/x-www-form-urlencoded"       -d "grant_type=password&client_id=delivery-api&username=admin&password=admin123"
```
Copie o `access_token` do JSON de resposta.

## Chamadas da API (exemplos cURL)
> Substitua `TOKEN` pelo access_token obtido acima.

### Criar cliente (admin/write)
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Authorization: Bearer TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Luiz Nogueira","email":"luiz@example.com","phone":"+55 11 99999-9999"}'
```

### Listar clientes
```bash
curl -X GET http://localhost:8080/api/v1/customers -H "Authorization: Bearer TOKEN"
```

### Criar produto (admin/write)
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Camiseta Azul","price": 79.90}'
```

### Criar pedido (admin/write)
```bash
# suponha que você tenha um customerId e productId
curl -X POST http://localhost:8080/api/v1/orders \      -H "Authorization: Bearer TOKEN" -H "Content-Type: application/json" \      -d '{"customerId":"<uuid-do-cliente>","items":[{"productId":"<uuid-do-produto>","quantity":2}]}'
```

### Atualizar status do pedido (admin/write)
```bash
curl -X PATCH http://localhost:8080/api/v1/orders/<order-id>/status \      -H "Authorization: Bearer TOKEN" -H "Content-Type: application/json" \      -d '{"status":"SHIPPED"}'
```

### Listar pedidos (com filtro)
```bash
curl -X GET "http://localhost:8080/api/v1/orders?status=CREATED" -H "Authorization: Bearer TOKEN"
```

### Obter pedido por ID
```bash
curl -X GET http://localhost:8080/api/v1/orders/<order-id> -H "Authorization: Bearer TOKEN"
```

## Decisões de arquitetura
- **DTO manual**: Para clareza e reduzir dependências desnecessárias.
- **UUID nas entidades**: evita colisões e simplifica integração.
- **@CreationTimestamp** no pedido: garante data/hora de criação automática.
- **Cache Redis**: aplicado em `CustomerService` e `ProductService` para GET e listagens.
- **Security**: OAuth2 Resource Server com Keycloak. Regras via `@PreAuthorize`:
  - Leitura: `ROLE_delivery_user` ou escopo `delivery`/`delivery.read`
  - Escrita: `ROLE_delivery_admin` ou escopo `delivery.write`
- **Perfis**: default (H2), `docker` (Postgres/Redis/Keycloak via compose).

## Build/Test
```bash
gradle clean build
```

## Observações
- Caso prefira, gere o wrapper localmente: `gradle wrapper` (irá criar `./gradlew`).
- Ajuste o `KEYCLOAK_ISSUER_URI` se mudar portas/host.

---

© 2025 Delivery API
