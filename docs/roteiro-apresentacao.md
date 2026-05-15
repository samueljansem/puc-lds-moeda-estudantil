# Roteiro de Apresentação — Lab03S03

**Duração:** ~20 minutos. **Apresentadores:** Samuel Jansem. **Demo ao vivo
após os slides** (ver [DEMO.md](DEMO.md)).

---

## 0. Abertura (≈ 1 min)

- Nome do projeto: **Sistema de Moeda Estudantil**.
- Trabalho da disciplina **Laboratório de Desenvolvimento de Software**,
  PUC Minas, 2026/1. Lab03 — Sprint 03.
- Proposta de valor: reconhecer mérito acadêmico via uma moeda virtual.
  Professor distribui, aluno troca por vantagens em empresas parceiras.

---

## 1. O problema e os papéis (≈ 1 min)

- 4 atores: **Aluno**, **Professor**, **Empresa Parceira**, **Admin**.
- Fluxo de valor central:
  ```
  Admin → concede 1.000 moedas → Professor
  Professor → transfere com motivo → Aluno
  Empresa → cadastra Vantagem (foto, custo) → Catálogo
  Aluno → resgata Vantagem → Cupom (UUID)
  ```
- Mostrar slide **docs/diagrams/use-case.png** (23 casos de uso, 4 atores).

---

## 2. Tecnologias escolhidas (≈ 3 min)

| Camada | Tecnologia | Por quê |
| ------ | ---------- | ------- |
| Linguagem | **Java 21** (LTS) | virtual threads, records, switch expressions |
| Framework | **Micronaut 4.7** | startup rápido, DI compile-time, ergonomia próxima ao Spring |
| ORM | **Micronaut Data JPA + Hibernate 6.6** | gera DAOs por convenção, herança JOINED |
| Migrations | **Flyway** | schema versionado em SQL puro |
| Banco | **H2 2.3** (file mode) | zero-config, embedded, ideal para entrega acadêmica |
| Auth | **Micronaut Security + Session** | cookie de sessão; BCrypt cost 12 |
| Views | **Thymeleaf 3.x** server-rendered | sem SPA, sem REST/JSON |
| Build | **Gradle 9 (Kotlin DSL)** | wrapper baixa tudo automaticamente |
| Test | **JUnit 5 + Micronaut Test** | `@MicronautTest` sobe contexto real |

Decisão consciente: **não usamos SMTP**. "E-mails" são persistidos em uma
tabela `notificacao` (outbox) e visualizados em `/notificacoes` (própria
do usuário) ou `/admin/notificacoes` (global). Simplifica o demo e permite
exibir o status (`ENVIADA` / `FALHA`) — embaixo de US12.

---

## 3. Arquitetura: MVC + DAO + Service (≈ 3 min)

Mostrar slide **docs/diagrams/component.png**. Caminhar pelas camadas:

1. **View** (`src/main/resources/views/`): templates Thymeleaf
   server-renderizados, todos em pt-BR (FR-013). Layout único compartilhado
   via `th:fragment`.

2. **Controller** (`controller/`): 13 controllers Micronaut. Cada um
   anota `@Secured("ALUNO"|"PROFESSOR"|"EMPRESA_PARCEIRA"|"ADMIN")` para
   isolar papéis. Forms tradicionais (`application/x-www-form-urlencoded`)
   exceto upload de foto da Vantagem (`multipart/form-data`).

3. **Service** (`service/`): 8 serviços. Cada operação que **muta estado**
   está `@Transactional` — saldo, motivo e notificação caem juntos ou
   nenhum cai (FR-006, FR-011).

4. **DAO** (`dao/`): 11 repositórios. Padrão DAO preservado no nome,
   implementação delegada ao Micronaut Data JPA (`extends
   CrudRepository<T, Long>`). Queries derivadas por convenção
   (`findByCredencialLogin`, `findByEmpresaIdOrderByCriadaEmDesc`) ou
   `@Query` JPQL quando o filtro tem parâmetros opcionais
   (`VantagemDAO.filtrar`).

5. **Model** (`model/`): 11 entidades JPA. `Usuario` é abstrato; `Aluno`,
   `Professor`, `EmpresaParceira` herdam via `@Inheritance(JOINED)`. Mostrar
   slide **docs/diagrams/class.png**.

Pontos de design para destacar verbalmente:
- Por que `TransferenciaMoeda` e `Resgate` são entidades separadas
  (em vez de `Transacao` polimórfica): nullables e atributos divergentes
  por tipo (motivo vs. código). Modelo mais claro, schema mais estrito.
- Admin **não** tem linha em `usuario` — ele existe apenas em `credencial`
  com `tipo_ator='ADMIN'`. Evita criar uma 4ª subclasse JPA só para um
  papel sem dados pessoais.

---

## 4. Camada de persistência (≈ 4 min)

Mostrar slide **docs/diagrams/er.png** e abrir
`src/main/resources/db/migration/V1__create_schema.sql` no IDE.

### 4.1 Estratégia de mapeamento

- **JPA JOINED**: tabela pai `usuario` (id, nome, email, credencial_id,
  tipo, ativo) + uma tabela filha por subclasse concreta (`aluno`,
  `professor`, `empresa_parceira`). Cada tabela filha tem PK que **também
  é FK para usuario.id**.
- Discriminator column `tipo` (`ALUNO`, `PROFESSOR`, `EMPRESA_PARCEIRA`).
- Trade-off vs. SINGLE_TABLE: JOIN extra em cada query, em troca de
  schema normalizado sem colunas nullable de outras subclasses.

### 4.2 Padrão DAO

Mostrar `AlunoDAO`:

```java
@Repository
public interface AlunoDAO extends CrudRepository<Aluno, Long> {
    Optional<Aluno> findByCpf(String cpf);
    Optional<Aluno> findByCredencialLogin(String login);
    List<Aluno> findByInstituicaoIdOrderByNome(Long instituicaoId);
    boolean existsByCpf(String cpf);
}
```

A implementação é gerada pelo Micronaut Data **em tempo de compilação**
(annotation processor) — não há reflexão em runtime. O nome "DAO" preserva
o vocabulário do Padrão DAO exigido pelo enunciado.

### 4.3 Migrations

5 migrations no `src/main/resources/db/migration/`:

| Versão | Conteúdo |
| ------ | -------- |
| V1     | Schema base (usuario, aluno, empresa_parceira, credencial, instituicao) |
| V2     | Seed das 5 instituições parceiras (FR-002)                              |
| V3     | Seed demo: demo.aluno + demo.empresa                                    |
| V4     | Schema Lab03S03: ALTER constraints + professor, vantagem, transferencia_moeda, resgate, notificacao, token_recuperacao |
| V5     | Seed Lab03S03: demo.admin + demo.professor (saldo 1000) + 3 vantagens demo |

Flyway aplica em ordem na subida da aplicação. `hbm2ddl.auto: validate`
em `application.yml` garante que as entidades JPA batem com o schema
migrado.

### 4.4 Atomicidade

Mostrar `ServicoMoeda.transferir`:

```java
@Transactional
public TransferenciaMoeda transferir(Long profId, Long alunoId, int valor, String motivo) {
    // validações
    professor.setSaldo(professor.getSaldo() - valor);
    aluno.setSaldo(aluno.getSaldo() + valor);
    transferenciaDAO.save(new TransferenciaMoeda(...));
    servicoNotificacao.enviar(aluno.getEmail(), ...);  // grava na outbox
    return transf;
}
```

Tudo dentro da mesma transação: **se a notificação falha, a
transferência rola back**. Garante FR-006 (atomicidade).

---

## 5. Demo ao vivo (≈ 7 min)

Seguir o roteiro de [DEMO.md](DEMO.md). Cobertura mínima:

1. **Login dos 4 papéis** (demo.aluno, demo.professor, demo.empresa,
   demo.admin) → redirect correto por role.
2. **Professor transfere 200 moedas** → notificação cai no `/notificacoes`
   do aluno.
3. **Aluno navega o catálogo, filtra por custo, resgata uma vantagem** →
   recebe cupom UUID; saldo cai; cupom aparece em `/alunos/cupons`.
4. **Empresa vê confirmação** com o mesmo código de verificação em
   `/notificacoes`.
5. **Empresa desativa uma vantagem** → ela some do catálogo público;
   tentativa de resgate retorna mensagem "vantagem indisponível"
   (US8 + casos de borda).
6. **Admin abre /admin/semestre** e clica em "Conceder" → saldo do
   professor sobe em +1000 (acumula sobre o saldo atual — FR-004).
7. **Recuperação de senha** (US7): aluno clica "Esqueci minha senha",
   admin abre `/admin/notificacoes`, copia o link de reset, navega para
   `/redefinir-senha?token=...`, define nova senha → login funciona.
8. **Reusar o mesmo token** → rejeitado com "Este link já foi usado".

---

## 6. Testes (≈ 1 min)

```bash
./gradlew test
```

≈ 30 testes cobrindo invariantes críticas:

- **DAO**: unicidade de CPF/CNPJ/login/codigo (FR-014, FR-015), seed
  Lab03S03 (`ProfessorDAOTest.demoProfessorTemSaldoInicialDeMil`).
- **Service** (foco em regras de negócio):
  - `ServicoMoedaTest`: saldo insuficiente, motivo vazio, atomicidade.
  - `ServicoResgateTest`: saldo insuficiente, vantagem desativada,
    códigos UUID distintos, filtro de catálogo.
  - `ServicoRecuperacaoSenhaTest`: token expirado, token usado, senha
    curta, troca BCrypt verificável.
- **Controller**: happy path + cenário pt-BR de erro para Aluno, Empresa,
  Login.

Filosofia: **invariantes críticas + happy paths**, sem perseguir % de
cobertura.

---

## 7. Encerramento (≈ 1 min)

- Constituição do projeto: **Simplicity First** + **Readable Code**.
  Decisões reflexionando isso:
  - Outbox interno em vez de SMTP (uma dependência a menos).
  - Duas tabelas para movimentações em vez de polimorfismo nullable.
  - Admin sem subclasse JPA (não precisava).
- Roadmap fora do escopo: SMTP real, dashboard de métricas, importação
  CSV de roster de professores, refresh tokens de sessão.
- Repositório:
  `https://github.com/samueljansem/puc-lds-lab-03`
- Perguntas?

---

## Notas para apresentação

- Manter o **terminal aberto** ao lado dos slides com `./gradlew run`
  já rodando antes de começar.
- Ter o navegador em uma janela secundária com `http://localhost:8080`
  em primeira aba e `http://localhost:8080/admin/notificacoes`
  (logado como admin) em outra.
- Caso o avaliador pergunte sobre detalhes que não cabem nos 20 min:
  - Foto da Vantagem: BLOB em coluna `vantagem.foto` + endpoint
    `GET /vantagens/{id}/foto` — sem filesystem.
  - Por que sessão e não JWT: o Lab pede MVC server-rendered.
  - Por que sem CSRF token: escopo acadêmico; documentado.
