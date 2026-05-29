-- Lab03S03 — seed de demonstração
-- Adiciona:
--   - Admin (`demo.admin` / `1234`) — só existe em `credencial`; o admin
--     não tem perfil pessoal, ele só aciona a tela de concessão semestral e
--     vê a caixa global de notificações.
--   - Professor (`demo.professor` / `1234`) — vinculado à PUC Minas,
--     departamento "Ciência da Computação", saldo inicial 1000 (FR-004).
--   - 3 vantagens demo cadastradas pela `demo.empresa`, sem foto (foto é
--     opcional no schema; o avaliador faz upload pela UI).
--
-- Hashes BCrypt gerados com cost=12 usando a mesma biblioteca da aplicação
-- (at.favre.lib:bcrypt 0.10.2).

-- === Admin demo ===
INSERT INTO credencial (login, senha_hash, tipo_ator) VALUES
    ('demo.admin', '$2a$12$o12A9AQzpMNF/3pU8TJggOvJKGNLRuEP9VXYp2wAv0NaulDPWBEeS', 'ADMIN');

-- === Professor demo: Maria Orientadora, PUC Minas ===
INSERT INTO credencial (login, senha_hash, tipo_ator) VALUES
    ('demo.professor', '$2a$12$zfl1l.rvBHZKa6ao1brYVueBJAZCs9QAGbRxQd6a1MKzOCEbF/rgC', 'PROFESSOR');

INSERT INTO usuario (nome, email, credencial_id, tipo, ativo)
SELECT 'Maria Orientadora',
       'demo.professor@exemplo.br',
       id,
       'PROFESSOR',
       TRUE
  FROM credencial
 WHERE login = 'demo.professor';

INSERT INTO professor (id, cpf, departamento, instituicao_id, saldo)
SELECT u.id,
       '22233344400',
       'Ciência da Computação',
       i.id,
       1000
  FROM usuario u, instituicao i
 WHERE u.email = 'demo.professor@exemplo.br'
   AND i.nome  = 'PUC Minas';

-- === Vantagens demo da Acme Demonstrações Ltda ===
INSERT INTO vantagem (empresa_id, descricao, custo, ativa, criada_em)
SELECT e.id, 'Desconto de 20% no restaurante universitário (até R$ 30)', 100, TRUE, CURRENT_TIMESTAMP
  FROM empresa_parceira e
  JOIN usuario u ON u.id = e.id
 WHERE u.email = 'demo.empresa@exemplo.br';

INSERT INTO vantagem (empresa_id, descricao, custo, ativa, criada_em)
SELECT e.id, 'Voucher de R$ 50 em material didático', 250, TRUE, CURRENT_TIMESTAMP
  FROM empresa_parceira e
  JOIN usuario u ON u.id = e.id
 WHERE u.email = 'demo.empresa@exemplo.br';

INSERT INTO vantagem (empresa_id, descricao, custo, ativa, criada_em)
SELECT e.id, 'Desconto de 5% na mensalidade do próximo semestre', 500, TRUE, CURRENT_TIMESTAMP
  FROM empresa_parceira e
  JOIN usuario u ON u.id = e.id
 WHERE u.email = 'demo.empresa@exemplo.br';
