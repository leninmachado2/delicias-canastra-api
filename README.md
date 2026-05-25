# Delícias Canastra API

Um pequeno projeto em Java Spring Boot para um catálogo de produtos artesanais com pedido e simulação de checkout PagBank.

## Visão geral

- Aplicação web em **Spring Boot 4.0.6** e **Java 21**
- Interface com **Thymeleaf** para exibir catálogo, carrinho e finalizar pedido
- Persistência em **H2** (banco em memória)
- Simulação de fluxo de pagamento via **PagBank**

## Funcionalidades atuais

- Exibição de catálogo de produtos com nome, descrição, preço e imagem
- Carrinho de compras no lado cliente com total atualizado
- Tela de confirmação antes de enviar o pedido
- Envio do pedido para backend via POST em `/api/pedidos`
- Geração de link de checkout PagBank a partir do pedido

## Estrutura principal

- `src/main/java/br/com/emporiocanastradf/delicias_canastra_api`
  - `controller/` - controla rotas e endpoints REST
  - `service/` - lógica de pedido e pagamento
  - `repository/` - acesso a dados com Spring Data JPA
  - `model/` - entidades `Cliente`, `Pedido`, `PedidoItem`, `Produto`
- `src/main/resources/templates/index.html` - página principal do catálogo
- `src/main/resources/application.properties` - configurações do Spring Boot
- `pom.xml` - build Maven e dependências

## Tecnologias

- Java 21
- Spring Boot 4.0.6
- Thymeleaf
- Spring Data JPA
- H2 Database
- Bootstrap 5
- Maven Wrapper (`mvnw.cmd`)

## Como executar

No Windows PowerShell, a partir do diretório do projeto:

```powershell
.\\mvnw.cmd clean spring-boot:run
```

Ou para compilar apenas:

```powershell
.\\mvnw.cmd -DskipTests compile
```

## Observações

- O projeto está em desenvolvimento e já funciona como protótipo de catálogo e pedido.
- O checkout PagBank atual é representado por um link gerado a partir do pedido.
- O pacote base do projeto é `br.com.emporiocanastradf`.
