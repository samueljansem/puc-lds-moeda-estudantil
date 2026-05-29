---
marp: true
title: Sistema de Moeda Estudantil — Lab03S04
paginate: true
---

# 🎓 Sistema de Moeda Estudantil

**Lab03S04 — RabbitMQ no processamento de resgates**

PUC Minas · LDS 2026/1 · Samuel Jansem

<!--
15s. "Vou mostrar a integração de mensageria que entreguei nessa sprint
final. 10 minutos contados; a maior parte é demo."
-->

---

## Contexto em 30 segundos

- 4 atores: **Aluno**, **Professor**, **Empresa**, **Admin**
- Professor distribui moedas; Aluno troca por vantagens
- Cada resgate dispara 2 notificações (aluno + empresa)

**Stack:** Java 21 · Micronaut 4.7 · JPA + H2 · Thymeleaf · Flyway · **RabbitMQ 3**

<!--
30s. Só ancorar o domínio. Não detalhar — já foi nas sprints anteriores.
Se quiser, mostrar use-case.png por 5s.
-->

---

## Arquitetura geral

```
   Navegador (Thymeleaf)
          │  HTTP + HTML
          ▼
┌──────────────────────────────────────┐
│   Controllers    (MVC: C, 13 classes)│
│         │                            │
│   Services      (regras + @Transactional)
│         │                            │
│   DAOs          (Micronaut Data JPA) │
└─────────┬────────────────────┬───────┘
          │ JDBC               │ AMQP (novo no S04)
          ▼                    ▼
        H2 (file)           RabbitMQ
```

MVC clássico · padrão DAO · sem SPA, sem REST/JSON

<!--
45s. "MVC server-rendered, como o lab pede. O RabbitMQ é a única
dependência externa nova — entra no diagrama pela direita."
Diagrama completo está em docs/diagrams/component.png se a banca pedir.
-->

---

## O que mudou no S04

**Antes:** `ServicoNotificacao` persistia tudo direto como `ENVIADA`
— outbox em tabela, mas sem broker.

**Agora:** mesma operação, mas:

1. Linha entra como `PENDENTE`
2. **RabbitMQ** entrega via fanout pra 2 consumers
3. Consumers marcam `ENVIADA`

→ Padrão **Transactional Outbox** completo.

<!--
45s. Esse é o pivot da apresentação. Pause e respire aqui.
-->

---

## Transactional Outbox — o fluxo

```
Aluno clica "Resgatar"
        │
        ▼
┌──────────────────────────────────────┐
│ ServicoResgate (@Transactional)      │
│  • debita saldo                       │
│  • INSERT Resgate (UUID)              │
│  • INSERT notificacao status=PENDENTE │  ← outbox
└──────────────┬───────────────────────┘
               │
   cupom volta IMEDIATO pro aluno
               │
       (~2s depois)
               ▼
   Drainer @Scheduled → publish → RabbitMQ → Listeners → status=ENVIADA
```

**Banco = fonte da verdade.** Broker = transporte.

<!--
1min. O slide mais importante. Diga: "Tudo na mesma transação JPA. Se a
linha cair, o débito também rola back. Se o broker estiver fora, ninguém
perde mensagem."
-->

---

## Topologia: fanout pra 2 filas

```
                  ┌──────────────────┐
   publish    ──► │  notificacoes    │   exchange fanout
                  └──┬───────────┬───┘
                     │           │
              ┌──────▼────┐ ┌────▼────────┐
              │  .email   │ │  .webhook   │   filas
              └──────┬────┘ └────┬────────┘
                     │           │
              [EMAIL-SIM]   [WEBHOOK-SIM]      consumers
```

- Mesma mensagem em 2 filas, falhas isoladas por canal
- Side-effects simulados via log (sem SMTP/HTTP real)
- Idempotente: `UPDATE WHERE status='PENDENTE'`

<!--
45s. Show the management UI mentally — diga "tudo isso é visível em
localhost:15672 durante o demo".
-->

---

## Degraded mode — o ponto da outbox

A aplicação **sobe sem RabbitMQ**.

- Broker offline no boot → app sobe normalmente
- Broker cai em runtime → drainer aguarda, nada se perde
- Broker volta → reconecta sozinho, drena pendências

**Sem DLQ.** Retry vem do próprio outbox: linhas `PENDENTE` são
republicadas no próximo tick (2s).

<!--
45s. Vender o degraded mode. "Isso é a verdadeira razão da outbox: o
sistema continua funcional sem o broker."
-->

---

## Stack do messaging

| Componente              | Responsabilidade                     |
| ----------------------- | ------------------------------------ |
| `ServicoNotificacao`    | INSERT na outbox (`PENDENTE`)        |
| `DrainadorNotificacoes` | `@Scheduled(2s)`, publica pendentes  |
| `AmqpTopologia`         | Declara exchange + queues + bindings |
| `PublisherNotificacao`  | `@RabbitClient` (interface)          |
| `ListenerEmail/Webhook` | `@RabbitListener`, marca `ENVIADA`   |

5 classes novas · 1 migration · 0 mudanças no fluxo síncrono.

<!--
30s. Resumo de "quanto código foi". Banca gosta de ver economia.
-->

---

## 🔴 Demo ao vivo

1. `docker compose up -d` + `./gradlew run`
2. Login `demo.aluno` → catálogo → resgatar
3. Cupom imediato; `/notificacoes` mostra **Pendente**
4. (~2s) badge vira **Enviada** + logs `[EMAIL-SIM]` `[WEBHOOK-SIM]`
5. Painel RabbitMQ: `localhost:15672` mostra throughput
6. **Killer move:** `docker compose pause rabbitmq` → resgate continua
   funcionando → `unpause` → backlog drena automático

⏱ ~3 min

<!--
3min. Slide-apoio. Use o terminal e o browser, não o slide.
Browser preparado com 3 abas: catálogo, /notificacoes, management UI.
-->

---

## Decisões e Simplicity First

| Decisão                   | Por quê                          |
| ------------------------- | -------------------------------- |
| Outbox + relay            | Sem dual-write, retry built-in   |
| Fanout 2 queues           | Pub/sub real, falhas isoladas    |
| Payload só `{id}`         | DB é fonte da verdade            |
| Sem DLQ                   | Outbox já faz retry              |
| Logs em vez de SMTP/HTTP  | Simplicity First (universitário) |
| 44 testes (mocks, sem TC) | Rodam sem Docker                 |

<!--
30s. Cada linha é uma decisão consciente. Banca valoriza.
-->

---

# Obrigado!

📦 `github.com/samueljansem/puc-lds-lab-03`

❓ Perguntas?

<!--
30s + Q&A. Volte pro terminal se a banca quiser explorar.
-->
