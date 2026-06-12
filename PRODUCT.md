# Product

## Register

product

## Users

Three roles inside conveniated universities (PUC Minas and peers), all in pt-BR:

- **Alunos** — students, mostly on phones, in daylight, between classes. They
  check their coin balance, browse the perk catalog, and redeem coupons. They
  want to feel *recognized*, and to spend that recognition on something real.
- **Professores** — on a laptop, awarding coins to specific students with a
  mandatory written reason. Low frequency, high intent: each award is a
  deliberate act of recognition, not a click.
- **Empresas parceiras** — staff in an office managing the perks they offer
  and confirming redeemed coupons.

The job to be done: turn academic merit into something visible, auditable, and
exchangeable, without ceremony.

## Product Purpose

A virtual currency that makes student merit tangible. Each semester a professor
receives 1.000 accumulating coins and distributes them to students as
recognition, always *with a reason*. Students redeem coins for perks offered by
partner companies. Every coin movement is a row in an auditable ledger.

Success = the system feels trustworthy and clear enough that a professor awards
without hesitation, a student understands their balance and history at a glance,
and a grading professor can navigate every flow without a manual.

## Brand Personality

**Merit, made visible.** Three words: *credible, warm, deliberate.*

The product is a **recognition ledger**, not a gamified points app and not a
corporate fintech dashboard. The emotional core is the *reason* attached to
every coin: recognition is personal and earned. The interface should carry the
quiet authority of an official record (a diploma, a statement, a stamped seal)
while staying warm and human, because the subject is people acknowledging
people.

Voice: plain, respectful Brazilian Portuguese. States facts, never hypes.

## Anti-references

- **The slate + indigo SaaS reflex** — the previous look. Generic, could be any
  admin tool. Explicitly rejected.
- **Gold + navy "fintech / coin" cliché** — glossy gold coins, navy gradients,
  crypto energy. The currency here is academic merit, not money.
- **Education teal / friendly-rounded-blue** — the edtech default. Too soft,
  reads as childish for a system professors and companies use.
- **Gamified confetti / Duolingo-green** — rewards-app reflex. Recognition here
  is dignified, not a streak counter.

## Design Principles

1. **The reason is the soul.** Every coin carries a written motive. Surface it,
   never bury it. The ledger is human, not numeric noise.
2. **Recognition, not transaction.** Spending coins is redeeming earned reward,
   a healthy act. Never color it as loss. Reserve alarm (red) for real errors.
3. **Quiet authority.** Borrow the credibility of official documents: hairline
   rules, generous whitespace, confident typography. Earn trust by looking like
   a record, not an app chasing attention.
4. **Earned familiarity.** It is a task tool used by three roles, one of them a
   grader. Standard affordances, consistent vocabulary screen to screen, no
   reinvented controls. Delight lives in moments (the balance cover, the coin),
   never in friction.
5. **Simplicity is a constraint, not a fallback.** Server-rendered Thymeleaf,
   one hand-written CSS file, no framework, no JS build. The aesthetic must be
   achievable in plain CSS and stay readable for whoever grades it.

## Accessibility & Inclusion

- WCAG 2.1 AA contrast for text and interactive states on the paper surface.
  The laurel-green accent is used at weights that hold AA on paper.
- Visible focus ring on every interactive element (the project already does
  this; keep it).
- Status is never conveyed by color alone: dots, icons, and pt-BR text labels
  accompany every state pill and alert.
- Respect `prefers-reduced-motion`; motion only conveys state, never decorates.
- Fully responsive down to small phones (students are the primary mobile users).
