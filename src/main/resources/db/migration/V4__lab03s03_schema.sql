-- Lab03S03 — extensão do schema
-- Cobre todas as tabelas e alterações necessárias para a sprint:
--   1) Expande o enum TipoAtor para incluir ADMIN.
--   2) Adiciona `ativo` em usuario (soft delete — Fase 4).
--   3) Tabela `professor` (herança JOINED como aluno/empresa_parceira).
--   4) Tabela `vantagem` (com BLOB de foto).
--   5) Tabela `transferencia_moeda` (Professor → Aluno).
--   6) Tabela `resgate` (Aluno → Vantagem, com código único de cupom).
--   7) Tabela `notificacao` (outbox de e-mail, sem SMTP real).
--   8) Tabela `token_recuperacao` (reset de senha).

-- 1) Expande TipoAtor para incluir ADMIN.
ALTER TABLE credencial DROP CONSTRAINT ck_credencial_tipo;
ALTER TABLE credencial ADD CONSTRAINT ck_credencial_tipo
    CHECK (tipo_ator IN ('ALUNO', 'PROFESSOR', 'EMPRESA_PARCEIRA', 'ADMIN'));

ALTER TABLE usuario DROP CONSTRAINT ck_usuario_tipo;
ALTER TABLE usuario ADD CONSTRAINT ck_usuario_tipo
    CHECK (tipo IN ('ALUNO', 'PROFESSOR', 'EMPRESA_PARCEIRA'));
-- Nota: o admin vive apenas em `credencial`; não há linha em `usuario` para ele,
-- então o CHECK acima propositadamente NÃO inclui 'ADMIN'.

-- 2) Soft delete em usuario.
ALTER TABLE usuario ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

-- 3) Professor — herança JOINED (PK = FK para usuario).
CREATE TABLE professor (
    id              BIGINT       NOT NULL PRIMARY KEY,
    cpf             VARCHAR(11)  NOT NULL,
    departamento    VARCHAR(120) NOT NULL,
    instituicao_id  BIGINT       NOT NULL,
    saldo           INT          NOT NULL DEFAULT 0,
    CONSTRAINT uk_professor_cpf         UNIQUE (cpf),
    CONSTRAINT fk_professor_usuario     FOREIGN KEY (id)             REFERENCES usuario (id),
    CONSTRAINT fk_professor_instituicao FOREIGN KEY (instituicao_id) REFERENCES instituicao (id),
    CONSTRAINT ck_professor_saldo       CHECK (saldo >= 0)
);

-- 4) Vantagem — empresa parceira cadastra; aluno resgata.
CREATE TABLE vantagem (
    id                BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresa_id        BIGINT        NOT NULL,
    descricao         VARCHAR(500)  NOT NULL,
    custo             INT           NOT NULL,
    foto              BLOB,
    foto_content_type VARCHAR(50),
    ativa             BOOLEAN       NOT NULL DEFAULT TRUE,
    criada_em         TIMESTAMP     NOT NULL,
    CONSTRAINT fk_vantagem_empresa FOREIGN KEY (empresa_id) REFERENCES empresa_parceira (id),
    CONSTRAINT ck_vantagem_custo   CHECK (custo > 0)
);

-- 5) Transferência de moedas Professor → Aluno (US1).
CREATE TABLE transferencia_moeda (
    id            BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    professor_id  BIGINT        NOT NULL,
    aluno_id      BIGINT        NOT NULL,
    valor         INT           NOT NULL,
    motivo        VARCHAR(500)  NOT NULL,
    realizada_em  TIMESTAMP     NOT NULL,
    CONSTRAINT fk_transf_professor FOREIGN KEY (professor_id) REFERENCES professor (id),
    CONSTRAINT fk_transf_aluno     FOREIGN KEY (aluno_id)     REFERENCES aluno     (id),
    CONSTRAINT ck_transf_valor     CHECK (valor > 0)
);

-- 6) Resgate de vantagem (US5). O `codigo` é o número de verificação que aparece
-- nos dois e-mails (aluno + empresa) para conferência presencial — FR-015.
CREATE TABLE resgate (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    aluno_id     BIGINT       NOT NULL,
    vantagem_id  BIGINT       NOT NULL,
    custo        INT          NOT NULL,
    codigo       VARCHAR(36)  NOT NULL,
    realizado_em TIMESTAMP    NOT NULL,
    CONSTRAINT uk_resgate_codigo   UNIQUE (codigo),
    CONSTRAINT fk_resgate_aluno    FOREIGN KEY (aluno_id)    REFERENCES aluno    (id),
    CONSTRAINT fk_resgate_vantagem FOREIGN KEY (vantagem_id) REFERENCES vantagem (id),
    CONSTRAINT ck_resgate_custo    CHECK (custo > 0)
);

-- 7) Notificacao — "caixa de e-mail" da aplicação. Em vez de SMTP real, cada
-- evento que dispararia um e-mail (transferência, cupom de resgate, reset de
-- senha, etc.) grava uma linha aqui. O usuário enxerga as suas em /notificacoes
-- e o admin enxerga todas em /admin/notificacoes.
CREATE TABLE notificacao (
    id                BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    destinatario      VARCHAR(120)  NOT NULL,
    assunto           VARCHAR(255)  NOT NULL,
    corpo             VARCHAR(2000) NOT NULL,
    status            VARCHAR(20)   NOT NULL,
    codigo_referencia VARCHAR(36),
    criada_em         TIMESTAMP     NOT NULL,
    CONSTRAINT ck_notif_status CHECK (status IN ('ENVIADA', 'FALHA'))
);

-- 8) Token de recuperação de senha (US7). TTL controlado em código (1h);
-- `usado` marca consumo único.
CREATE TABLE token_recuperacao (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    credencial_id BIGINT       NOT NULL,
    token         VARCHAR(64)  NOT NULL,
    expira_em     TIMESTAMP    NOT NULL,
    usado         BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em     TIMESTAMP    NOT NULL,
    CONSTRAINT uk_token_token      UNIQUE (token),
    CONSTRAINT fk_token_credencial FOREIGN KEY (credencial_id) REFERENCES credencial (id)
);
