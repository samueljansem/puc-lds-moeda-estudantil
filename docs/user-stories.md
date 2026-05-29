# Especificação da Funcionalidade: Sistema de Moeda Estudantil

### História do Usuário 1 - Professor reconhece um aluno com moedas (Prioridade: P1)

Um professor deseja recompensar um aluno por bom comportamento, participação
em aula ou mérito acadêmico. O professor faz login, seleciona o aluno, escolhe
quantas moedas enviar, digita um motivo obrigatório descrevendo por que o
aluno está sendo reconhecido e submete a transferência. O aluno recebe uma
notificação por e-mail em pt-BR sobre as moedas e o motivo, e o professor
recebe um e-mail de confirmação do envio.

**Por que essa prioridade**: Esta é a proposta de valor central do sistema —
sem ela, não há reconhecimento de mérito, não há moedas chegando aos alunos
e não há motivo para alunos ou empresas parceiras se engajarem. É a fatia
MVP.

**Teste Independente**: Popule um professor com saldo e um aluno matriculado,
faça login como o professor, submeta uma transferência com um motivo válido
e verifique se o saldo do aluno aumentou e se uma notificação por e-mail foi
emitida em pt-BR.

**Cenários de Aceitação**:

1. **Dado** um professor com saldo ≥ 10 e um aluno matriculado,
   **Quando** o professor submete uma transferência de 10 moedas com um
   motivo não vazio, **Então** o saldo do aluno aumenta em 10, o saldo do
   professor diminui em 10, e o aluno recebe um e-mail em pt-BR contendo o
   motivo.
2. **Dado** um professor com saldo de 5 moedas, **Quando** o professor tenta
   transferir 10 moedas, **Então** a transferência é rejeitada e o professor
   vê uma mensagem em pt-BR indicando saldo insuficiente.
3. **Dado** um professor com saldo suficiente, **Quando** o professor submete
   uma transferência com um motivo vazio, **Então** a transferência é
   rejeitada e o professor vê uma mensagem em pt-BR indicando que o motivo é
   obrigatório.

---

### História do Usuário 2 - Aluno se cadastra e faz login (Prioridade: P1)

Um aluno em potencial se cadastra no sistema fornecendo nome, e-mail, CPF,
RG, endereço, instituição de ensino (selecionada de uma lista pré-cadastrada)
e curso. Após o cadastro, o aluno pode fazer login com login e senha e
acessar sua conta.

**Por que essa prioridade**: Sem o cadastro e a autenticação do aluno,
nenhum aluno pode receber moedas, então a US1 não consegue entregar valor de
ponta a ponta.

**Teste Independente**: Com a lista de instituições pré-carregada, complete o
formulário de cadastro como um novo aluno, faça login e chegue à área da
conta.

**Cenários de Aceitação**:

1. **Dado** que a lista de instituições está pré-carregada, **Quando** um
   novo aluno submete o formulário de cadastro com todos os campos
   obrigatórios e um e-mail e CPF válidos, **Então** a conta é criada e o
   aluno consegue fazer login com suas credenciais.
2. **Dado** um aluno existente cadastrado com um determinado CPF, **Quando**
   um novo cadastro é tentado com o mesmo CPF, **Então** o cadastro é
   rejeitado com uma mensagem em pt-BR indicando que o CPF já está em uso.

---

### História do Usuário 3 - Aluno e Professor consultam seu extrato (Prioridade: P2)

Um aluno ou professor deseja ver seu saldo atual de moedas e a lista de
transações em que esteve envolvido. Para um professor, isso mostra moedas
enviadas (para quem, quando, quantas, motivo). Para um aluno, isso mostra
moedas recebidas (de quem, quando, quantas, motivo) e resgates realizados
(qual vantagem, quando, quantas moedas, código de verificação).

**Por que essa prioridade**: A transparência do saldo e do histórico de
transações constrói confiança no sistema. Não é exigida pelo fluxo de
recompensa central, mas é necessária para confirmar e auditar a atividade.

**Teste Independente**: Após uma transferência de um professor para um
aluno, faça login como cada um e confirme que ambos os extratos mostram a
mesma transação com detalhes consistentes.

**Cenários de Aceitação**:

1. **Dado** um professor que transferiu moedas para dois alunos, **Quando**
   o professor abre seu extrato, **Então** ele vê seu saldo atual e uma
   lista cronológica de ambas as transferências com valor, destinatário,
   motivo e data.
2. **Dado** um aluno que recebeu moedas e resgatou uma vantagem, **Quando**
   o aluno abre seu extrato, **Então** ele vê seu saldo atual, as
   transações de recebimento e a transação de resgate com seu código de
   verificação.

---

### História do Usuário 4 - Empresa Parceira se cadastra e cadastra vantagens (Prioridade: P2)

Uma empresa parceira se auto-cadastra com suas informações de identificação
e credenciais de login. Após fazer login, a empresa cadastra vantagens (por
exemplo, descontos em restaurantes universitários, desconto de mensalidade,
compra de materiais específicos) com uma descrição, foto do produto e custo
em moedas.

**Por que essa prioridade**: Sem vantagens cadastradas, os alunos não têm
nada para resgatar, então a US5 não consegue entregar valor. Pode ser
construída após US1–US2 estarem prontas.

**Teste Independente**: Cadastre uma nova empresa parceira, faça login,
cadastre uma vantagem com os campos obrigatórios e verifique se ela se
torna visível no catálogo voltado ao aluno.

**Cenários de Aceitação**:

1. **Dado** que não existe um cadastro para um determinado CPF/CNPJ de
   empresa, **Quando** a empresa submete o formulário de cadastro com dados
   válidos, **Então** a conta da empresa é criada e a empresa consegue
   fazer login.
2. **Dado** uma empresa parceira autenticada, **Quando** ela cadastra uma
   vantagem com descrição, foto e custo em moedas, **Então** a vantagem
   aparece no catálogo voltado ao aluno.

---

### História do Usuário 5 - Aluno resgata uma vantagem (Prioridade: P2)

Um aluno navega pelas vantagens cadastradas e seleciona uma para resgatar,
desde que seu saldo cubra o custo. Em um resgate bem-sucedido, o saldo do
aluno é debitado pelo custo, o aluno recebe um e-mail de cupom com um
código de verificação gerado pelo sistema, e a empresa parceira recebe um
e-mail de confirmação com o mesmo código para verificação presencial.

**Por que essa prioridade**: Isto completa o ciclo de recompensa. Entrega o
benefício tangível que justifica o aluno se importar com moedas em primeiro
lugar e depende de US1, US2 e US4 para existir.

**Teste Independente**: Popule um aluno com saldo suficiente, cadastre uma
vantagem em uma empresa parceira, resgate a vantagem como o aluno e
verifique o débito do saldo, mais ambos os e-mails contendo o mesmo código
de verificação.

**Cenários de Aceitação**:

1. **Dado** um aluno com saldo ≥ custo de uma vantagem, **Quando** o aluno
   resgata a vantagem, **Então** seu saldo é decrescido pelo custo, ele
   recebe um e-mail em pt-BR contendo um código de verificação único, e a
   empresa parceira recebe um e-mail de confirmação em pt-BR contendo o
   mesmo código de verificação.
2. **Dado** um aluno com saldo < custo de uma vantagem, **Quando** o aluno
   tenta resgatá-la, **Então** o resgate é rejeitado com uma mensagem em
   pt-BR indicando saldo insuficiente.

---

### História do Usuário 6 - Aluno edita seus dados de perfil (Prioridade: P3)

Um aluno autenticado deseja atualizar seus próprios dados cadastrais
(endereço, curso, e-mail) sem precisar refazer o cadastro. O aluno faz
login, acessa seu perfil, edita os campos permitidos e salva. Os dados de
identificação imutáveis (CPF, RG) não podem ser alterados.

**Por que essa prioridade**: Mantém o cadastro do aluno atualizado ao
longo do tempo (mudança de endereço, troca de curso). Não bloqueia o
fluxo de mérito, mas é esperada em qualquer sistema que mantenha
cadastro de pessoas.

**Teste Independente**: Faça login como um aluno existente, altere o
endereço, salve, faça logout e login novamente — o endereço atualizado
deve persistir.

**Cenários de Aceitação**:

1. **Dado** um aluno autenticado, **Quando** ele altera seu endereço e
   salva, **Então** o novo endereço passa a ser exibido em consultas
   subsequentes.
2. **Dado** um aluno autenticado, **Quando** ele tenta alterar seu CPF,
   **Então** o sistema impede a alteração com uma mensagem em pt-BR
   indicando que o CPF é imutável.
3. **Dado** um aluno autenticado, **Quando** ele submete um e-mail já
   utilizado por outro cadastro, **Então** a alteração é rejeitada com
   uma mensagem em pt-BR indicando que o e-mail já está em uso.

---

### História do Usuário 7 - Aluno recupera senha por e-mail (Prioridade: P3)

Um aluno que esqueceu sua senha solicita a recuperação informando seu
e-mail cadastrado. O sistema envia um e-mail em pt-BR com um link de
redefinição de senha contendo um token de uso único. Ao clicar no link,
o aluno define uma nova senha e pode autenticar-se com ela.

**Por que essa prioridade**: Recuperação de senha é um requisito básico
de qualquer sistema autenticado. Mitiga o risco de usuários ficarem
trancados fora do sistema. Estende C02 (autenticar-se).

**Teste Independente**: Solicite recuperação de senha para um aluno
existente, capture o token enviado, defina uma senha nova e autentique
com ela.

**Cenários de Aceitação**:

1. **Dado** um aluno cadastrado, **Quando** solicita recuperação de
   senha pelo seu e-mail, **Então** recebe um e-mail em pt-BR com um
   link contendo token de uso único.
2. **Dado** um token válido e não expirado, **Quando** o aluno define
   uma nova senha, **Então** ele consegue autenticar-se com a nova
   senha e o token é invalidado.
3. **Dado** um token expirado ou já utilizado, **Quando** o aluno tenta
   redefinir a senha, **Então** o sistema rejeita com uma mensagem em
   pt-BR e oferece nova solicitação.

---

### História do Usuário 8 - Empresa parceira edita ou desativa uma vantagem (Prioridade: P2)

Uma empresa parceira autenticada deseja corrigir dados de uma vantagem
cadastrada (descrição, foto, custo) ou retirá-la temporariamente do
catálogo (desativação) sem perder o histórico de resgates já realizados.

**Por que essa prioridade**: Permite que a empresa mantenha o catálogo
saudável (corrigir typos, ajustar custo, retirar produtos esgotados)
sem comprometer a integridade do histórico. Estende C07 (cadastrar
vantagem).

**Teste Independente**: Como empresa parceira autenticada, edite uma
vantagem existente alterando seu custo; em seguida, desative-a e
verifique que ela some do catálogo público mas continua referenciada
nos resgates anteriores.

**Cenários de Aceitação**:

1. **Dado** uma empresa parceira autenticada com uma vantagem
   cadastrada, **Quando** ela altera o custo da vantagem, **Então** o
   novo custo passa a valer para resgates futuros, sem afetar resgates
   anteriores.
2. **Dado** uma vantagem com resgates já realizados, **Quando** a
   empresa a desativa, **Então** ela deixa de aparecer no catálogo
   público, mas as transações anteriores continuam acessíveis no
   extrato dos alunos que a resgataram.
3. **Dado** uma empresa autenticada, **Quando** ela tenta editar uma
   vantagem de outra empresa, **Então** a operação é rejeitada com uma
   mensagem em pt-BR indicando ausência de permissão.

---

### História do Usuário 9 - Aluno filtra o catálogo de vantagens (Prioridade: P3)

Um aluno autenticado deseja encontrar vantagens compatíveis com seu
saldo ou de uma empresa específica sem precisar percorrer o catálogo
inteiro. Ele aplica filtros de custo máximo e/ou empresa parceira e
visualiza apenas as vantagens correspondentes.

**Por que essa prioridade**: Melhora a usabilidade do catálogo conforme
o número de vantagens cresce. Não bloqueia o fluxo principal, mas é
esperada em qualquer interface de e-commerce. Estende C09 (consultar
catálogo).

**Teste Independente**: Cadastre 5 vantagens de duas empresas diferentes
com custos variados; como aluno, aplique filtro de custo máximo e
verifique que só as vantagens dentro do limite aparecem.

**Cenários de Aceitação**:

1. **Dado** um catálogo com vantagens de custos variados, **Quando** o
   aluno aplica filtro de custo máximo X, **Então** o sistema exibe
   apenas vantagens com `custo ≤ X`.
2. **Dado** um catálogo com vantagens de várias empresas, **Quando** o
   aluno seleciona uma empresa específica no filtro, **Então** o
   sistema exibe apenas vantagens daquela empresa.
3. **Dado** filtros que não retornam nenhuma vantagem, **Quando** o
   aluno aplica os filtros, **Então** o sistema exibe uma mensagem em
   pt-BR indicando que nenhuma vantagem foi encontrada.

---

### História do Usuário 10 - Professor lista alunos da sua instituição (Prioridade: P2)

Um professor autenticado deseja visualizar a lista de alunos da sua
instituição antes de selecionar um para receber moedas. A lista exibe
nome, curso e e-mail de cada aluno.

**Por que essa prioridade**: Apoia o fluxo de C04 (enviar moedas)
permitindo que o professor identifique o aluno certo sem precisar
saber o ID/CPF antecipadamente. Estende C04.

**Teste Independente**: Cadastre 3 alunos em uma instituição; faça
login como professor da mesma instituição e verifique que os 3 alunos
aparecem na lista. Cadastre 1 aluno em outra instituição e verifique
que ele NÃO aparece.

**Cenários de Aceitação**:

1. **Dado** um professor autenticado vinculado a uma instituição,
   **Quando** ele acessa a lista de alunos, **Então** o sistema exibe
   todos os alunos da mesma instituição com nome, curso e e-mail.
2. **Dado** alunos de instituições diferentes da do professor, **Quando**
   ele acessa a lista, **Então** esses alunos NÃO são exibidos.

---

### História do Usuário 11 - Aluno consulta cupons emitidos (Prioridade: P3)

Um aluno autenticado deseja revisar os cupons de resgate que já emitiu,
incluindo código de verificação e status (utilizado / pendente), sem
precisar buscar no extrato geral.

**Por que essa prioridade**: Facilita o uso presencial do cupom — o
aluno consegue achar rapidamente o código que precisa apresentar na
empresa parceira. Estende C03 (consultar extrato).

**Teste Independente**: Como aluno com 2 resgates realizados, acesse a
seção de cupons e verifique que ambos aparecem com seus códigos de
verificação corretos.

**Cenários de Aceitação**:

1. **Dado** um aluno que realizou pelo menos um resgate, **Quando** ele
   acessa a seção "Meus Cupons", **Então** o sistema exibe cada cupom
   com vantagem associada, data, código de verificação e status.
2. **Dado** um aluno sem resgates realizados, **Quando** ele acessa a
   seção, **Então** o sistema exibe uma mensagem em pt-BR indicando
   que ele ainda não possui cupons.

---

### História do Usuário 12 - Aluno é informado de falha na entrega de cupom (Prioridade: P2)

Um aluno realizou um resgate, mas o e-mail de cupom falhou na entrega
(caixa cheia, e-mail inválido, etc.). O aluno deve ser informado da
falha sem perder o saldo já debitado, e deve poder solicitar reenvio
após corrigir seu e-mail.

**Por que essa prioridade**: Protege o aluno contra perda de moedas por
falha de infraestrutura externa. Estende C11 (receber cupom). Já está
nos casos de borda do spec, mas o tratamento merece ser história
independente porque envolve UI (notificação visível) e fluxo de
correção.

**Teste Independente**: Force a falha de envio (e-mail inválido), faça
o resgate; verifique que o saldo foi debitado E que a falha está
visível no extrato/cupons; corrija o e-mail e solicite reenvio; o
e-mail deve ser entregue com sucesso.

**Cenários de Aceitação**:

1. **Dado** um aluno cujo e-mail está inválido, **Quando** ele realiza
   um resgate, **Então** o saldo é debitado, a transação é registrada
   e o sistema marca o cupom com status "falha de entrega" visível
   para o aluno.
2. **Dado** um cupom com falha de entrega, **Quando** o aluno corrige
   seu e-mail e solicita reenvio, **Então** o sistema dispara nova
   tentativa e atualiza o status do cupom.

---

### Casos de Borda

- O saldo do professor é mantido no início de um novo semestre: as 1.000
  moedas concedidas para o novo semestre são adicionadas ao saldo
  remanescente, elas NÃO o zeram.
- Professor tenta transferir para um aluno que não pertence ao sistema: a
  transferência é rejeitada com uma mensagem em pt-BR.
- Professor tenta transferir para um aluno de outra instituição: a
  transferência é rejeitada com uma mensagem em pt-BR (um professor só
  reconhece alunos da sua própria instituição).
- Professor tenta transferir para um aluno inativo (cadastro desativado):
  a transferência é rejeitada com uma mensagem em pt-BR.
- Empresa parceira exclui ou desativa uma vantagem enquanto um aluno está
  visualizando-a: a tentativa de resgate é rejeitada com uma mensagem em
  pt-BR "vantagem indisponível".
- E-mail ou CPF duplicado durante o cadastro (aluno, upload do roster de
  professor ou empresa parceira): o cadastro é rejeitado com uma mensagem
  clara em pt-BR identificando o campo duplicado.
- Falha na entrega de e-mail: a transação subjacente DEVE permanecer
  registrada; o sistema expõe a falha de entrega ao usuário afetado sem
  reverter a transação.

## Requisitos _(obrigatório)_

### Requisitos Funcionais

- **FR-001**: O sistema DEVE permitir que alunos se auto-cadastrem
  fornecendo nome, e-mail, CPF, RG, endereço, instituição de ensino e
  curso.
- **FR-002**: Alunos DEVEM selecionar sua instituição a partir de uma
  lista pré-cadastrada; instituições em texto livre NÃO DEVEM ser aceitas.
- **FR-003**: O sistema DEVE carregar professores a partir de um roster
  fornecido por cada instituição parceira; professores NÃO DEVEM se
  auto-cadastrar. Cada registro de professor DEVE armazenar nome, CPF,
  departamento e a instituição vinculada.
- **FR-004**: No início de cada semestre, todo professor DEVE receber
  1.000 moedas. Quaisquer moedas não gastas em semestres anteriores DEVEM
  ser preservadas, de modo que o saldo se acumule em vez de ser zerado.
- **FR-005**: Um professor DEVE ser capaz de transferir moedas para um
  aluno específico apenas quando (a) seu saldo atual cobrir o valor e (b)
  um motivo não vazio for fornecido.
- **FR-006**: Em uma transferência bem-sucedida, o sistema DEVE
  atomicamente debitar o saldo do professor, creditar o saldo do aluno,
  registrar a transação e enviar notificações por e-mail em pt-BR: ao
  aluno (recebimento, contendo o motivo) e ao professor (confirmação do
  envio, contendo o motivo e o saldo restante).
- **FR-007**: Alunos e professores DEVEM ser capazes de consultar seu
  extrato, exibindo seu saldo atual e uma lista cronológica de transações
  (professor: transferências enviadas; aluno: moedas recebidas e resgates
  realizados).
- **FR-008**: Empresas parceiras DEVEM ser capazes de se auto-cadastrar
  com suas informações de identificação e credenciais de login.
- **FR-009**: Empresas parceiras DEVEM ser capazes de cadastrar vantagens,
  cada uma incluindo uma descrição, uma foto do produto e um custo em
  moedas.
- **FR-010**: Alunos DEVEM ser capazes de navegar pelo catálogo de
  vantagens cadastradas e selecionar uma para resgatar, desde que seu
  saldo cubra o custo.
- **FR-011**: Em um resgate bem-sucedido, o sistema DEVE atomicamente
  debitar o saldo do aluno pelo custo, registrar a transação, enviar um
  e-mail de cupom em pt-BR ao aluno contendo um código de verificação
  gerado pelo sistema, e enviar um e-mail de confirmação em pt-BR à
  empresa parceira contendo o mesmo código.
- **FR-012**: Alunos, professores e empresas parceiras DEVEM cada um se
  autenticar via login e senha antes de acessar qualquer funcionalidade
  protegida.
- **FR-013**: Todo texto voltado ao usuário (rótulos, formulários,
  mensagens, erros, e-mails) DEVE estar em pt-BR.
- **FR-014**: O sistema DEVE prevenir cadastro duplicado por CPF (alunos,
  professores, empresas parceiras) e por e-mail; tentativas duplicadas
  DEVEM ser rejeitadas com uma mensagem clara em pt-BR identificando o
  campo duplicado.
- **FR-015**: O sistema DEVE gerar um código de verificação único para
  cada resgate; o mesmo código DEVE aparecer no e-mail de cupom do aluno e
  no e-mail de confirmação da empresa parceira para apoiar a conferência
  presencial.

### Entidades Principais

- **Aluno**: Um estudante que participa do sistema de mérito. Atributos:
  nome, e-mail, CPF, RG, endereço, instituição, curso, saldo de moedas,
  credenciais. Relacionamentos: pertence a uma Instituição; recebe moedas
  de Professores; resgata Vantagens de Empresas Parceiras.
- **Professor**: Um docente pré-cadastrado via roster institucional.
  Atributos: nome, CPF, departamento, saldo de moedas, credenciais.
  Relacionamentos: pertence a uma Instituição; transfere moedas para
  Alunos.
- **Instituição**: Uma instituição educacional parceira pré-cadastrada no
  sistema. Atributos: informações de identificação. Relacionamentos:
  contém Alunos e Professores.
- **Empresa Parceira**: Uma empresa parceira que oferece vantagens.
  Atributos: informações de identificação, credenciais. Relacionamentos:
  possui Vantagens.
- **Vantagem**: Um benefício que alunos podem resgatar. Atributos:
  descrição, foto do produto, custo em moedas. Relacionamentos: pertence a
  uma Empresa Parceira.
- **Transação**: Uma movimentação de moedas. Atributos: tipo
  (transferência | resgate), data, valor, e ou motivo (para
  transferência) ou código de verificação (para resgate). Relacionamentos:
  referencia o Aluno, Professor, Vantagem e Empresa Parceira envolvidos
  conforme aplicável.
- **Credencial**: Login e senha vinculados a Aluno, Professor ou Empresa
  Parceira para autenticação.

## Critérios de Sucesso _(obrigatório)_

### Resultados Mensuráveis

- **SC-001**: Um professor consegue completar uma transferência de moedas
  — do login à confirmação — em menos de 2 minutos na primeira tentativa.
- **SC-002**: 100% das transferências de moedas disparam e-mails de
  notificação ao aluno (recebimento) e ao professor (confirmação de
  envio) em até 1 minuto após a confirmação.
- **SC-003**: Um aluno consegue completar um resgate — do login ao
  recebimento do e-mail de cupom — em menos de 3 minutos na primeira
  tentativa.
- **SC-004**: 100% das strings voltadas ao usuário (rótulos, formulários,
  erros, e-mails) são apresentadas em pt-BR.
- **SC-005**: 95% dos alunos e empresas parceiras que se cadastram pela
  primeira vez completam o cadastro sem precisar de assistência externa.
- **SC-006**: 100% das transferências de moedas tentadas com saldo
  insuficiente ou motivo vazio são rejeitadas; 100% dos resgates tentados
  com saldo insuficiente são rejeitados.
- **SC-007**: 100% dos resgates produzem códigos de verificação
  correspondentes no e-mail de cupom do aluno e no e-mail de confirmação
  da empresa parceira.

## Premissas

- A lista de instituições parceiras e o roster de cada professor são
  carregados por um processo administrativo que está fora do escopo desta
  especificação.
- Um "semestre" segue o calendário acadêmico institucional; a concessão
  de 1.000 moedas é aplicada automaticamente no início de cada semestre,
  sem ação manual de administrador.
- Cadastros de aluno e empresa parceira são auto-atendidos e entram em
  vigor imediatamente, sem uma etapa manual de aprovação.
- A entrega de e-mail usa um canal padrão de e-mail transacional; o
  sistema não é responsável pelo resultado de entregabilidade além de
  registrar uma tentativa e expor a falha.
- Uploads de fotos para vantagens são tratados via uploads padrão de
  formulário web; limites específicos de formato e tamanho são decididos
  em /speckit-plan.
- Esta especificação descreve o Sistema de Moeda Estudantil completo. Os
  entregáveis do sprint Lab03S01 (diagrama de casos de uso, histórias do
  usuário, diagrama de classes, diagrama de componentes) serão produzidos
  durante /speckit-plan com base nesta especificação.
