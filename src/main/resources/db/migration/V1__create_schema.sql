-- Lab03S02 — schema inicial
-- Cria as 5 tabelas necessárias para os CRUDs de Aluno e EmpresaParceira:
-- usuario (pai), aluno, empresa_parceira (filhas via JOINED),
-- credencial, instituicao.
--
-- Tabelas restantes (professor, vantagem, transacao, notificacao,
-- linha_extrato) entram em migrations da Lab03S03.

CREATE TABLE credencial (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login       VARCHAR(80)  NOT NULL,
    senha_hash  VARCHAR(255) NOT NULL,
    tipo_ator   VARCHAR(20)  NOT NULL,
    CONSTRAINT uk_credencial_login UNIQUE (login),
    CONSTRAINT ck_credencial_tipo  CHECK (tipo_ator IN ('ALUNO', 'PROFESSOR', 'EMPRESA_PARCEIRA'))
);

CREATE TABLE instituicao (
    id    BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome  VARCHAR(120) NOT NULL,
    CONSTRAINT uk_instituicao_nome UNIQUE (nome)
);

CREATE TABLE usuario (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(120) NOT NULL,
    email          VARCHAR(120) NOT NULL,
    credencial_id  BIGINT       NOT NULL,
    tipo           VARCHAR(20)  NOT NULL,
    CONSTRAINT uk_usuario_email      UNIQUE (email),
    CONSTRAINT uk_usuario_credencial UNIQUE (credencial_id),
    CONSTRAINT fk_usuario_credencial FOREIGN KEY (credencial_id) REFERENCES credencial (id),
    CONSTRAINT ck_usuario_tipo       CHECK (tipo IN ('ALUNO', 'PROFESSOR', 'EMPRESA_PARCEIRA'))
);

CREATE TABLE aluno (
    id              BIGINT       NOT NULL PRIMARY KEY,
    cpf             VARCHAR(11)  NOT NULL,
    rg              VARCHAR(20)  NOT NULL,
    endereco        VARCHAR(255) NOT NULL,
    curso           VARCHAR(120) NOT NULL,
    instituicao_id  BIGINT       NOT NULL,
    saldo           INT          NOT NULL DEFAULT 0,
    CONSTRAINT uk_aluno_cpf          UNIQUE (cpf),
    CONSTRAINT fk_aluno_usuario      FOREIGN KEY (id)             REFERENCES usuario (id),
    CONSTRAINT fk_aluno_instituicao  FOREIGN KEY (instituicao_id) REFERENCES instituicao (id),
    CONSTRAINT ck_aluno_saldo        CHECK (saldo >= 0)
);

CREATE TABLE empresa_parceira (
    id    BIGINT      NOT NULL PRIMARY KEY,
    cnpj  VARCHAR(14) NOT NULL,
    CONSTRAINT uk_empresa_cnpj    UNIQUE (cnpj),
    CONSTRAINT fk_empresa_usuario FOREIGN KEY (id) REFERENCES usuario (id)
);
