# Design

Visual system for the **Caderneta** redesign of the Sistema de Moeda Estudantil.
A merit passbook: deep laurel-green book cloth, cool ruled-ledger paper, and a
single gold coin reserved for the currency itself. All tokens live in `:root`
in `src/main/resources/public/css/app.css`. One file, no framework, no JS.

## Theme

Light, cool. The surface is green-tinted ledger paper; text is green-black
registry ink. The metaphor is the caderneta: the small green passbook where
every deposit is written down and stamped, used in daylight on phones and
laptops.

## Color

OKLCH throughout. Every neutral is tinted toward green (hue ~145–160). Color
strategy is **Committed**: neutrals dominate the page surface while deep laurel
carries identity with real saturation — the header spine, the balance cover,
primary actions, links, and selected states. Gold appears only as the minted
coin, never as text.

### Neutrals (ledger paper & registry ink)

| Token | OKLCH | Role |
|---|---|---|
| `--paper` | `oklch(0.975 0.006 145)` | page background |
| `--surface` | `oklch(0.995 0.003 145)` | cards / pages of the book |
| `--surface-sunken` | `oklch(0.955 0.008 148)` | sunken panels, filters |
| `--ink` | `oklch(0.24 0.02 160)` | primary text, headings, header rules |
| `--ink-2` | `oklch(0.42 0.018 158)` | secondary text, table headers |
| `--ink-3` | `oklch(0.54 0.016 156)` | muted labels, captions |
| `--line` | `oklch(0.90 0.012 150)` | hairline borders, row rules |
| `--line-strong` | `oklch(0.83 0.016 150)` | input borders, stronger rules |

### Laurel (brand green — merit, registry, the passbook)

| Token | OKLCH | Role |
|---|---|---|
| `--laurel` | `oklch(0.46 0.09 155)` | primary buttons, links, accents |
| `--laurel-strong` | `oklch(0.40 0.085 155)` | hover / pressed |
| `--laurel-ink` | `oklch(0.39 0.08 155)` | green text on tint, perk costs |
| `--laurel-tint` | `oklch(0.955 0.025 152)` | row hover, selection, stamp bg |
| `--laurel-tint-line` | `oklch(0.885 0.04 152)` | border on tinted areas |

### Cover (the book cloth) and gold (the coin)

| Token | OKLCH | Role |
|---|---|---|
| `--cover` | `oklch(0.33 0.06 158)` | header spine, balance hero background |
| `--cover-ink` | `oklch(0.965 0.012 120)` | cream text on the cover |
| `--cover-ink-2` | `oklch(0.85 0.03 140)` | muted cream (labels, nav) |
| `--gold` | `oklch(0.78 0.125 88)` | coin glyph, engraved cover frame |
| `--gold-deep` | `oklch(0.62 0.115 80)` | 3px trim rule under the header |

Gold is decorative only (glyph, filete, trim) — it never carries text, so it
never needs to pass text contrast.

### Feedback

- **Sucesso / recebido** — green kin to the laurel (`oklch(0.53 0.11 152)`,
  ink `0.41`, tint `0.955`): receiving merit *is* the brand act.
- **Aviso / pendente** — ochre `oklch(0.68 0.115 75)`, tint `oklch(0.955 0.045 85)`.
- **Erro / perigo** — crimson `oklch(0.54 0.17 22)`, tint `oklch(0.955 0.03 20)`.
  Far from the brand hue; appears only in labelled alerts and destructive
  actions. Status is never color-only: every pill and alert carries a dot/icon
  and pt-BR text.

**Ledger amounts:** received is green; **spent is neutral ink, not red** —
redeeming a reward is healthy, not a loss.

## Typography

One family plus a system mono. Loaded from Google Fonts.

- **Archivo** (variable: `wdth` 62–125, `wght` 400–700) carries everything:
  body, forms, buttons, tables, pills. Display moments — page `h1`, the
  wordmark, the balance figure — use the same family **expanded**
  (`font-stretch: var(--largura-display)` = 118%), giving an institutional
  signage voice without a second font.
- **Mono — system stack** (`ui-monospace, "SF Mono", Menlo, monospace`) for
  coupon and verification codes.

Fixed rem scale, ratio ~1.2 with a wider jump at the display end:

`--t-xs 0.75` · `--t-sm 0.8125` · `--t-base 0.9375` · `--t-md 1` · `--t-lg 1.1875` ·
`--t-xl 1.5` · `--t-2xl 2` · `--t-3xl 2.75` (rem).

Prose capped at ~65ch; tables may run denser. Tabular numerals on all amounts.

## Geometry, elevation, motion

- **Radius:** `--r-sm 3px` · `--r-md 6px` · `--r-lg 10px` · `--r-pill 999px`.
  Squarer than typical app chrome — registry, not toy.
- **Borders do the structural work.** Cool hairlines (`--line`) define edges;
  shadows are sparing and green-tinted (`--shadow-sm/md/lg`).
- **Motion:** `--ease: cubic-bezier(0.22, 1, 0.36, 1)`, 150–200 ms. State only,
  never decoration. No animated layout properties. `prefers-reduced-motion`
  honored.
- **Focus:** 3px translucent laurel ring everywhere; cream ring inside the
  dark header (`.topo`).

## Components

Class contract preserved from the original markup so every Thymeleaf view
restyles without template churn.

- **Header (`.topo`)** — the book's spine: deep `--cover` green, cream
  wordmark and nav, finished with a 3px `--gold-deep` trim rule.
- **Brand coin** — flat gold disc with a deep-green `M` and a thin inner ring,
  inline SVG in the header and as the `--moeda` data-URI glyph on balances and
  perk costs. Flat, minted, no gradients.
- **Card / page (`.card`, `--narrow` 460, default 760, `--wide` 1080)** — a
  page of the book: near-white surface, hairline border, faint shadow.
- **Balance hero (`.saldo-card`)** — the passbook cover: drenched `--cover`
  green, gold coin glyph, uppercase cream label, expanded-Archivo figure in
  cream, and an engraved gold filete (inner 1px frame, inset 5px). The one
  delight moment.
- **Buttons** — primary = solid laurel with cream text; secondary = surface +
  hairline; ghost = bare. Full default/hover/active/disabled/focus set.
- **Tables (`.tabela-extrato`)** — the ruled ledger page: 2px ink rule under
  the header (like a printed form), hairline row rules, no zebra, laurel-tint
  row hover, tabular numerals.
- **Status pills / alerts** — tinted background + matching border + dot/icon +
  text, per feedback family.
- **Empty states (`.vazio`)** — dashed frame, ringed icon, teaching copy.
- **Catalog (`.galeria-vantagens` / `.card-vantagem`)** — responsive grid of
  perk cards with photo, title, gold-coin cost in laurel ink, company, redeem
  button.
- **Verification code (`.codigo-destaque`)** — the stamp on the page: mono,
  letterspaced, laurel ink on laurel tint.
