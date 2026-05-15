# 🎬 Roteiro de Demonstração

Guia rápido para o avaliador conferir o **Sistema de Moeda Estudantil** sem
precisar criar dados do zero. Tempo estimado: **5 minutos**.

---

## 1. Subir a aplicação

```bash
./gradlew run
```

Aguarde a linha:

```
INFO  io.micronaut.runtime.Micronaut - Startup completed in <N>ms. Server Running: http://localhost:8080
```

Abra **<http://localhost:8080>** no navegador.

> A primeira execução baixa Gradle 9.x + dependências (~1 min). Execuções
> seguintes sobem em ~1.5s.

---

## 2. Credenciais já populadas

A migration `V3__seed_demo.sql` cria dois usuários prontos:

| Perfil      | Login            | Senha           |
| ----------- | ---------------- | --------------- |
| 🎓 Aluno    | `demo.aluno`     | `aluno1234`     |
| 🏢 Empresa  | `demo.empresa`   | `empresa1234`   |

Cinco instituições estão pré-cadastradas (PUC Minas, UFMG, CEFET-MG, UFOP,
UNI-BH) — disponíveis no dropdown do cadastro de aluno.

---

## 3. Roteiro sugerido

### 3.1 Fluxo do Aluno (≈ 2 min)

1. **Login** → `http://localhost:8080/login`
   - Login: `demo.aluno` · Senha: `aluno1234`
   - Após o login você cai em `/` e é redirecionado para `/alunos/perfil`.
2. **Ver perfil** — confira nome, e-mail, CPF, RG, endereço, curso, instituição
   e saldo (= 0).
3. **Editar perfil** — clique em "Editar perfil", altere o **endereço** e/ou
   **curso** e salve. Volta para o perfil com os dados atualizados.
4. **Tentar trocar o e-mail para um já em uso** — edite o perfil colocando
   `demo.empresa@exemplo.br` e salve. O sistema rejeita com a mensagem em
   pt-BR _"Este e-mail já está em uso por outro cadastro"_.
5. **Logout** — botão "Sair" no topo. Volta para `/login`.

### 3.2 Fluxo da Empresa Parceira (≈ 1 min)

1. **Login** com `demo.empresa` / `empresa1234`.
2. **Ver perfil** — nome ("Acme Demonstrações Ltda"), e-mail, CNPJ.
3. **Editar perfil** — alterar o **nome** e/ou **e-mail**, salvar.
4. **Logout**.

### 3.3 Cadastro do zero (≈ 2 min)

1. Sair (se estiver logado) e acessar `/alunos/cadastro`.
2. Tentar cadastrar com **CPF inválido** (menos de 11 dígitos) → validação
   pt-BR aparece.
3. Tentar cadastrar com **CPF `11122233300`** (já é o do aluno demo) →
   sistema rejeita com _"Este CPF já está cadastrado"_ (FR-014).
4. Cadastrar com dados novos válidos:
   - Nome, e-mail, CPF de 11 dígitos, RG, endereço, curso
   - Selecionar uma instituição do dropdown
   - Login + senha (mínimo 6 caracteres)
5. Após sucesso, redireciona para `/login?cadastrado=1` mostrando _"Cadastro
   realizado com sucesso"_. Faça login com a nova credencial.
6. Repetir para `/empresas/cadastro` se quiser conferir o fluxo de empresa.

---

## 4. O que cada cenário demonstra

| Cenário                              | Requisito coberto                                     |
| ------------------------------------ | ----------------------------------------------------- |
| Login + redirecionamento por papel   | FR-012 (autenticação por login/senha)                 |
| Cadastro de aluno com instituição    | FR-001, FR-002 (instituição pré-cadastrada)           |
| Cadastro de empresa parceira         | FR-008                                                |
| Edição de perfil (US6)               | History US6                                           |
| CPF/CNPJ/e-mail/login únicos         | FR-014                                                |
| Mensagens de erro pt-BR              | FR-013                                                |
| Senhas armazenadas com hash BCrypt   | (segurança; verificável na tabela `credencial`)       |
| Schema versionado em Flyway + ER     | Lab03S02 — estratégia de persistência                 |

---

## 5. Inspecionando o banco (opcional)

Após `./gradlew run`, o banco fica em `./data/moedaestudantil.mv.db`. Para
abrir com o **H2 Console**:

```bash
java -jar ~/.gradle/caches/modules-2/files-2.1/com.h2database/h2/*/*/h2-*.jar
```

(ou baixe `h2-*.jar` direto da [página do H2](https://h2database.com/)).

Conexão:
- **JDBC URL:** `jdbc:h2:./data/moedaestudantil` (dentro do diretório do projeto)
- **User:** `sa`
- **Password:** _(em branco)_

Tabelas relevantes:
- `usuario` — pai da hierarquia (JPA `JOINED`)
- `aluno`, `empresa_parceira` — filhas, PK = FK para `usuario.id`
- `credencial` — login + `senha_hash` (BCrypt)
- `instituicao` — semeada em `V2__seed_instituicoes.sql`
- `flyway_schema_history` — histórico das migrations aplicadas

> O servidor Micronaut precisa estar **parado** para o H2 Console abrir o
> arquivo (ele bloqueia escrita exclusiva em modo arquivo).

---

## 6. Resetar o banco

Para zerar tudo e voltar ao estado inicial:

```bash
rm -rf data/
./gradlew run
```

As 3 migrations rodam de novo (`V1` schema, `V2` instituições, `V3` demo).

---

## 7. Rodar os testes

```bash
./gradlew test
```

Resultado esperado: **`BUILD SUCCESSFUL`** com ~19 testes (ver `README.md`
seção Testes para o que cada classe cobre).
