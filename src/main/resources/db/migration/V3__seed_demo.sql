-- Lab03S02 — seed de demonstração
-- Cria 1 aluno e 1 empresa parceira pré-cadastrados para o avaliador testar
-- a aplicação sem precisar passar pelo fluxo de cadastro primeiro.
--
-- Credenciais:
--   Aluno   — login: demo.aluno    senha: 1234
--   Empresa — login: demo.empresa  senha: 1234
--
-- Hashes BCrypt foram gerados com cost=12 (mesmo cost usado em ServicoCadastro).

-- === Aluno demo: João Demonstração, PUC Minas ===
INSERT INTO credencial (login, senha_hash, tipo_ator) VALUES
    ('demo.aluno', '$2a$12$/WB8H60VFu83a2wGnXa6gOyixCFr.7n8dg/9ExPMyJol8Okn9FKlm', 'ALUNO');

INSERT INTO usuario (nome, email, credencial_id, tipo)
SELECT 'João Demonstração',
       'demo.aluno@exemplo.br',
       id,
       'ALUNO'
  FROM credencial
 WHERE login = 'demo.aluno';

INSERT INTO aluno (id, cpf, rg, endereco, curso, instituicao_id, saldo)
SELECT u.id,
       '11122233300',
       'MG-DEMO-1',
       'Rua das Demonstrações, 100 — Belo Horizonte/MG',
       'Sistemas de Informação',
       i.id,
       0
  FROM usuario u, instituicao i
 WHERE u.email = 'demo.aluno@exemplo.br'
   AND i.nome  = 'PUC Minas';

-- === Empresa parceira demo: Acme Demonstrações ===
INSERT INTO credencial (login, senha_hash, tipo_ator) VALUES
    ('demo.empresa', '$2a$12$Yx96fmeXYVvXYNjzvBrmnetNbObcwng8h33kGkFTyxt/sU5j6gvUq', 'EMPRESA_PARCEIRA');

INSERT INTO usuario (nome, email, credencial_id, tipo)
SELECT 'Acme Demonstrações Ltda',
       'demo.empresa@exemplo.br',
       id,
       'EMPRESA_PARCEIRA'
  FROM credencial
 WHERE login = 'demo.empresa';

INSERT INTO empresa_parceira (id, cnpj)
SELECT id, '11222333000144'
  FROM usuario
 WHERE email = 'demo.empresa@exemplo.br';
