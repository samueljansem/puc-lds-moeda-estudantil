## Visão geral

Projeto Micronaut 4.7 + Java 21 + Thymeleaf MVC server-rendered, com Micronaut
Data JPA (Hibernate) + Flyway sobre H2 em modo arquivo. Veja `README.md` para
instruções de execução. Diagramas em `docs/diagrams/` (use-case, classe,
componentes, ER).

## Constituição do projeto

Os princípios que guiam o código e as decisões estão em
[`.specify/memory/constitution.md`](.specify/memory/constitution.md):

1. **Simplicity First** — a solução mais simples que funciona; sem abstrações
   prematuras; nenhuma dependência adicionada "por precaução".
2. **Readable Code** — o código precisa ser entendido por quem lê pela
   primeira vez (inclusive o professor que corrige); nomes claros, funções
   pequenas, arquivos curtos; comentários apenas quando o "por quê" não é
   óbvio.
