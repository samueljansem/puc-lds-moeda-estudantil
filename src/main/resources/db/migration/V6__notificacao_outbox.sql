-- Lab03S04 — outbox: notificação entra como PENDENTE e o consumer marca ENVIADA.
-- FALHA removido: o retry vem do drainer (próximo tick republica), não de DLQ.

ALTER TABLE notificacao DROP CONSTRAINT ck_notif_status;

ALTER TABLE notificacao ADD CONSTRAINT ck_notif_status
    CHECK (status IN ('PENDENTE', 'ENVIADA'));
