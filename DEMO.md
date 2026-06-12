# 🎬 Roteiro de Demonstração

Guia rápido para o avaliador conferir o **Sistema de Moeda Estudantil** sem
precisar criar dados do zero. Tempo estimado: **5 minutos**.

---

## 1. Subir a aplicação

```bash
# Broker para o pipeline de notificações
docker compose up -d

# Aplicação
./gradlew run
```

Aguarde a linha:

```
INFO  io.micronaut.runtime.Micronaut - Startup completed in <N>ms. Server Running: http://localhost:8080
```

Abra **<http://localhost:8080>** no navegador. O painel do RabbitMQ
está em **<http://localhost:15672>** (guest / guest).

> A primeira execução baixa Gradle 9.x + dependências (~1 min). Execuções
> seguintes sobem em ~1.5s.

---

## 2. Credenciais já populadas

As migrations `V3__seed_demo.sql` e `V5__seed_lab03s03_demo.sql` criam
quatro usuários prontos:

| Perfil         | Login              | Senha           |
| -------------- | ------------------ | --------------- |
| 🎓 Aluno       | `demo.aluno`       | `1234`          |
| 🏢 Empresa     | `demo.empresa`     | `1234`          |
| 🧑‍🏫 Professor   | `demo.professor`   | `1234`          |
| 🛂 Admin       | `demo.admin`       | `1234`          |

Cinco instituições estão pré-cadastradas (PUC Minas, UFMG, CEFET-MG, UFOP,
UNI-BH). `demo.professor` está vinculado à PUC Minas e nasce com **saldo
1.000** (FR-004). `demo.empresa` já tem **3 vantagens demo** cadastradas
(custos 100, 250 e 500 moedas).

---

## 3. Roteiro sugerido

### 3.1 Fluxo do Aluno (≈ 2 min)

1. **Login** → `http://localhost:8080/login`
   - Login: `demo.aluno` · Senha: `1234`
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

1. **Login** com `demo.empresa` / `1234`.
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

### 3.4 Fluxo do Professor — Transferir Moedas (≈ 2 min)

1. **Login** com `demo.professor` / `1234`. Você vai para
   `/professores/perfil`. Saldo inicial: **1.000 moedas**.
2. **Transferir moedas** — clique em "Transferir moedas". Selecione
   _João Demonstração_ no dropdown (alunos da mesma instituição —
   FR-002 + US10), informe 200 moedas e um motivo (ex.: "Excelente
   apresentação"). Submeta.
3. Redireciona para `/professores/extrato` com mensagem de sucesso. A
   transferência aparece na tabela (data, destinatário, valor negativo,
   motivo).
4. **Notificações do professor** — clique em "Notificações". O e-mail
   "Confirmação: você enviou 200 moedas" está lá, com o aluno, o motivo e
   o saldo restante (template do professor — Lab04S01).
5. **Validar restrições** — tente transferir com motivo em branco
   (rejeitado) e com valor > saldo (rejeitado com mensagem pt-BR
   "Saldo insuficiente").
6. **Lista de alunos** (US10) — clique em "Alunos da instituição" no
   perfil. Veja os alunos da PUC Minas com nome, curso, e-mail e saldo.

### 3.5 Fluxo do Aluno — Extrato, Catálogo e Resgate (≈ 3 min)

1. **Login** com `demo.aluno` / `1234`. Saldo: **200 moedas**
   (recebidas no passo 3.4).
2. **Ver extrato** — clique em "Extrato". A transferência aparece como
   "Recebido" com motivo.
3. **Notificações** — clique em "Notificações". O e-mail "Você recebeu
   200 moedas" está lá com status `ENVIADA`.
4. **Catálogo de vantagens** — clique em "Ver catálogo". 3 vantagens
   aparecem como cards. Aplique filtro **custo máximo = 200** → só
   "Desconto de 20%" (100 moedas) aparece (US9).
5. **Resgatar** "Desconto de 20%" → redireciona para `/alunos/cupons`
   com flash mostrando o **código UUID**. Saldo cai para 100.
6. **Meus Cupons** mostra o resgate com data, vantagem, empresa, custo
   e o código de verificação (US11).

### 3.6 Fluxo da Empresa Parceira — Vantagens e Resgates (≈ 2 min)

1. **Login** com `demo.empresa` / `1234`. Clique em "Minhas
   vantagens".
2. As 3 vantagens demo aparecem (todas Ativa). Clique em "Cadastrar nova
   vantagem" e envie uma vantagem com descrição, custo e (opcional) foto
   JPEG/PNG. A foto vira BLOB no banco (servida por
   `GET /vantagens/{id}/foto`).
3. **Editar** uma vantagem existente (alterar custo). A nova vantagem com
   custo alterado vale para resgates futuros; os resgates anteriores
   preservam o custo histórico (US8 cenário 1).
4. **Desativar** uma vantagem → ela some do catálogo público do aluno
   mas continua nos cupons já emitidos.
5. **Notificações** — abra `/notificacoes` (link no perfil). A
   confirmação do resgate feito no passo 3.5 está lá com o mesmo código
   UUID que o aluno tem no cupom (FR-015).

### 3.7 Fluxo do Admin — Concessão Semestral e Caixa Global (≈ 1 min)

1. **Login** com `demo.admin` / `1234`. Você vai para
   `/admin/inicio`.
2. **Conceder semestre** — clique no link, depois em "Conceder 1.000
   moedas a todos os professores". A tabela mostra o `demo.professor`
   com saldo atualizado (acumula sobre o saldo atual — FR-004).
3. **Caixa global de notificações** — todas as notificações que o
   sistema enviou (transferências, cupons, recuperações de senha) estão
   listadas com data, destinatário, assunto, status e mensagem completa.

### 3.8 Recuperação de Senha (US7) (≈ 2 min)

1. **Logout**.
2. Acesse `/login` e clique em "Esqueci minha senha".
3. Informe `demo.aluno@exemplo.br` e submeta. Redireciona com mensagem
   "se o e-mail estiver cadastrado…" (silencioso, não revela cadastros).
4. **Login como admin** para capturar o token (no demo não temos SMTP):
   `/admin/notificacoes` → procure o assunto "Recuperação de senha",
   copie o link `http://localhost:8080/redefinir-senha?token=...`.
5. Cole o link no navegador, defina nova senha (mínimo 6 caracteres).
6. **Login com a nova senha** — funciona. **Login com a antiga** — falha.
   Tentar **reusar o mesmo link** → mensagem "Este link já foi usado".

### 3.9 Trocar Senha (autenticado) e Desativar Conta (≈ 1 min)

1. Logado como `demo.aluno` (ou empresa, ou professor), clique em
   "Trocar senha". Informe a senha atual + nova senha + confirmação.
   Após o sucesso, faça logout e login com a nova.
2. No perfil do aluno (ou empresa), clique em "Desativar minha conta"
   no final da página. Confirme o `confirm()` do navegador. Você é
   redirecionado para `/logout`. Tentar logar novamente falha com
   _"Login ou senha inválidos"_ (na verdade `USER_DISABLED`).

---

## 4. O que cada cenário demonstra

| Cenário                                          | Requisito coberto                              |
| ------------------------------------------------ | ---------------------------------------------- |
| Login + redirecionamento por 4 papéis            | FR-012 (autenticação por login/senha)          |
| Cadastro de aluno com instituição                | FR-001, FR-002                                 |
| Cadastro de empresa parceira                     | FR-008                                         |
| Professor seed (sem auto-cadastro)               | FR-003                                         |
| Saldo do professor 1000 inicial + acumulação     | FR-004                                         |
| Transferência com motivo, saldo, atomicidade     | FR-005, FR-006, US1                            |
| Cadastro/edição/desativação de Vantagem          | FR-009, US4, US8                               |
| Catálogo com filtros                             | FR-010, US9                                    |
| Resgate atômico + dupla notificação + UUID único | FR-011, FR-015, US5                            |
| Extrato consolidado                              | FR-007, US3                                    |
| Cupons emitidos                                  | US11                                           |
| Recuperação de senha por token TTL 1h            | US7                                            |
| Trocar senha autenticado                         | (CRUD final)                                   |
| Desativar conta com soft delete                  | (CRUD final)                                   |
| Listagem de alunos por instituição               | US10                                           |
| Edição de perfil                                 | US6                                            |
| CPF/CNPJ/e-mail/login únicos                     | FR-014                                         |
| Mensagens em pt-BR (UI + notificações)           | FR-013                                         |
| Senhas armazenadas com hash BCrypt cost 12       | (segurança; tabela `credencial`)               |
| Schema versionado em Flyway + ER (5 migrations)  | Lab03S02/S03 — estratégia de persistência      |

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
- `usuario` — pai da hierarquia (JPA `JOINED`), com `ativo` (soft delete)
- `aluno`, `empresa_parceira`, `professor` — filhas, PK = FK para `usuario.id`
- `credencial` — login + `senha_hash` (BCrypt), inclui ADMIN
- `instituicao` — semeada em `V2__seed_instituicoes.sql`
- `vantagem` — cadastradas pela empresa; foto BLOB; soft delete via `ativa`
- `transferencia_moeda` — Professor → Aluno
- `resgate` — Aluno → Vantagem, com `codigo` UUID único
- `notificacao` — outbox interno (sem SMTP)
- `token_recuperacao` — reset de senha, TTL 1h, uso único
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

As 6 migrations rodam de novo (`V1` schema, `V2` instituições, `V3`
demo Lab03S02, `V4` schema Lab03S03, `V5` seed Lab03S03, `V6` outbox).

---

## 7. Rodar os testes

```bash
./gradlew test
```

Resultado esperado: **`BUILD SUCCESSFUL`** com ~44 testes (ver `README.md`
seção Testes para o que cada classe cobre).

---

## 8. RabbitMQ — *killer demo* de mensageria

Esse é o roteiro pra mostrar o padrão **Transactional Outbox** ao vivo —
o sistema continua funcionando mesmo com o broker fora do ar. O efeito fica
muito mais claro com **dois logs lado a lado**: o da aplicação (publisher +
os dois consumers) e o do broker (contadores das filas).

### 8.0. Logs lado a lado com tmux

**Pré-requisitos:** `tmux`, `watch`, `jq` e `curl`
(no macOS: `brew install tmux watch jq`).

Os níveis de log já vêm prontos em `src/main/resources/logback.xml`: os
consumers (`ListenerEmail` / `ListenerWebhook`) logam em `INFO` e o publisher
(`DrainadorNotificacoes`) em `DEBUG` — é a linha `Outbox: …` que mostra a
publicação saindo do outbox.

Abra uma sessão tmux com **3 panes** (`Ctrl-b %` divide na vertical,
`Ctrl-b "` divide na horizontal):

```bash
tmux new -s moeda
```

**Pane 1 — Aplicação** · sobe o servidor e espelha o log num arquivo:

```bash
docker compose up -d
./gradlew run 2>&1 | tee /tmp/moeda-app.log
```

**Pane 2 — Pipeline de notificação** · publisher + os 2 consumers, filtrado:

```bash
tail -f /tmp/moeda-app.log \
  | grep --line-buffered -E 'Outbox:|EMAIL-SIM|WEBHOOK-SIM|Broker indispon'
```

**Pane 3 — Broker RabbitMQ** · contadores das duas filas, a cada 1s:

```bash
watch -n 1 -t '
  resp=$(curl -s --max-time 2 -u guest:guest \
    "http://localhost:15672/api/queues/%2F?columns=name,messages,message_stats.publish,message_stats.deliver_get")
  if [ -n "$resp" ]; then
    echo "$resp" | jq -r ".[] | \"  \(.name)  publicadas=\(.message_stats.publish // 0)  entregues=\(.message_stats.deliver_get // 0)  prontas=\(.messages)\""
  else
    echo "  (broker fora do ar — management API nao respondeu)"
  fi
'
```

> **Sem `jq`?** Troque a linha do `jq` por
> `tr ',' '\n' | grep -E '"name"|"messages"|"publish"|"deliver_get"'`.
> **Prefere o navegador?** O mesmo dado, com gráfico de taxa, está em
> **<http://localhost:15672>** → aba *Queues* (login guest / guest).

Com os 3 panes abertos, dispare qualquer ação que gere notificação (um
resgate ou uma transferência). No **Pane 2** aparece primeiro
`Outbox: N notificação(ões) pendente(s) — publicando…` (o publisher) e em
seguida as linhas `[EMAIL-SIM]` / `[WEBHOOK-SIM]` (os consumers). Como cada
notificação faz *fan-out* para as duas filas, **um resgate gera 1 linha de
publicação + 4 linhas de consumo** (2 notificações × 2 filas). No **Pane 3**
os contadores `publicadas` e `entregues` sobem **igualmente nas duas filas** —
a prova visual do fanout.

### 8.1. Fluxo normal (broker ligado)

1. Logar como `demo.aluno` e resgatar uma vantagem.
2. Cupom aparece **imediatamente** na tela (caminho síncrono — débito de
   saldo, geração de UUID, persistência do `Resgate`).
3. Abrir `/notificacoes` em outra aba — as duas notificações aparecem
   inicialmente com badge cinza **Pendente**.
4. Em ~2 segundos, o drainer publica no exchange `notificacoes` e os dois
   listeners marcam **Enviada** (badge verde).
5. No **Pane 2** (pipeline) aparece a sequência completa publisher → consumers:
   ```
   DEBUG DrainadorNotificacoes - Outbox: 2 notificação(ões) pendente(s) — publicando…
   INFO  ListenerEmail   - [EMAIL-SIM]   to=demo.aluno@...   subject="Cupom de resgate — ..." code=<uuid>
   INFO  ListenerWebhook - [WEBHOOK-SIM] POST /parceira/notify body=(id:N, codigo:"<uuid>", assunto:"...")
   INFO  ListenerEmail   - [EMAIL-SIM]   to=demo.empresa@... subject="Resgate confirmado — ..." code=<uuid>
   INFO  ListenerWebhook - [WEBHOOK-SIM] POST /parceira/notify body=(id:N, codigo:"<uuid>", assunto:"...")
   ```
6. No **Pane 3** (broker), os contadores `publicadas` / `entregues` das filas
   `notificacoes.email` e `notificacoes.webhook` sobem juntos (mesmo no painel
   <http://localhost:15672> → *Queues*, com gráfico de throughput).

### 8.2. Degraded mode (broker fora do ar)

1. `docker compose pause rabbitmq` — broker congelado. No **Pane 3** o painel
   passa a mostrar `(broker fora do ar — management API não respondeu)`, já
   que a pausa congela também a management API.
2. Logar como `demo.aluno`, resgatar uma vantagem.
3. Cupom continua aparecendo **imediatamente** (caminho síncrono inalterado).
4. `/notificacoes` mostra as duas linhas em **Pendente** — e ficam assim.
5. No **Pane 2**: `WARN ... Broker indisponível ao publicar id=N.
   Republicação no próximo tick.` aparecendo a cada 2 segundos (o publisher
   tentando e desistindo, sem perder a notificação).
6. `docker compose unpause rabbitmq` — broker volta (Pane 3 volta a responder).
7. Em até 2 segundos, o **Pane 2** dispara as linhas `Outbox: …` →
   `[EMAIL-SIM]` / `[WEBHOOK-SIM]`, as linhas pendentes viram **Enviada** e os
   contadores do **Pane 3** sobem.

**O que isso prova:** o `INSERT` na tabela `notificacao` é a fonte da
verdade transacional; o RabbitMQ é apenas o transporte. Nenhuma
mensagem é perdida se o broker cair — o drainer republica
automaticamente quando ele voltar.
