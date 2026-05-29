# Sistema de Moeda Estudantil

Sistema que reconhece o mérito estudantil por meio de uma moeda virtual:
professores distribuem moedas a alunos, que as trocam por vantagens oferecidas
por empresas parceiras. Este glossário fixa a linguagem do domínio.

## Language

### Atores

**Aluno**:
Estudante vinculado a uma **Instituição** que recebe **Moedas** de **Professores**
e as troca por **Vantagens**. Possui **Saldo**.
_Avoid_: estudante, usuário, discente

**Professor**:
Docente pré-cadastrado por uma **Instituição** que distribui **Moedas** a
**Alunos**. Possui **Saldo**.
_Avoid_: docente, mestre

**Empresa Parceira**:
Empresa que cadastra **Vantagens** e confere **Resgates** presencialmente.
_Avoid_: parceiro, fornecedor, loja

**Instituição**:
Instituição de ensino pré-cadastrada à qual **Alunos** e **Professores** pertencem.
_Avoid_: faculdade, escola, universidade (use o termo genérico)

### Moeda e movimentações

**Moeda**:
Unidade virtual de reconhecimento de mérito. Inteira (não fracionária).
_Avoid_: ponto, crédito, token

**Saldo**:
Quantidade de **Moedas** que um **Aluno** ou **Professor** possui no momento.
_Avoid_: balanço, total

**Transferência**:
Movimentação de **Moedas** de um **Professor** para um **Aluno**, sempre com um
**Motivo** obrigatório. É a única forma de um aluno adquirir moedas.
_Avoid_: envio (como substantivo do registro), doação, pagamento

**Motivo**:
Texto livre e obrigatório que justifica uma **Transferência**.
_Avoid_: justificativa, descrição, comentário

**Concessão Semestral**:
Crédito automático de 1.000 **Moedas** ao **Saldo** de cada **Professor** no
início de cada semestre. Acumula sobre o saldo remanescente — nunca o zera.
_Avoid_: recarga, renovação, reset

**Resgate**:
Troca de **Moedas** de um **Aluno** por uma **Vantagem**, que debita o **Saldo**
e gera um **Cupom**.
_Avoid_: compra, troca, pedido

**Vantagem**:
Benefício (desconto, produto) que uma **Empresa Parceira** oferece por um custo
em **Moedas**.
_Avoid_: produto, oferta, prêmio, recompensa

**Cupom**:
Comprovante de um **Resgate**, identificado por um **Código de Verificação**
único, apresentado presencialmente na **Empresa Parceira**.
_Avoid_: voucher, ticket, bilhete

**Extrato**:
Visão do **Saldo** atual mais a lista cronológica de movimentações de um ator
(para o **Professor**: transferências enviadas; para o **Aluno**: moedas
recebidas e resgates realizados).
_Avoid_: histórico, relatório, balanço

### Notificações

**Notificação**:
Mensagem por e-mail emitida por um evento do sistema. Persistida e entregue de
forma assíncrona (a entrega real é simulada nesta versão).
_Avoid_: aviso, alerta, mensagem

**Recebimento** _(evento de notificação)_:
Notificação enviada ao **Aluno** quando ele recebe uma **Transferência**.
Contém o professor remetente, o valor, o **Motivo** e o novo **Saldo**.

**Confirmação de Envio** _(evento de notificação)_:
Notificação enviada ao **Professor** quando ele realiza uma **Transferência**.
Contém o aluno destinatário, o valor, o **Motivo** e o **Saldo** restante.

## Flagged ambiguities

- **"Notificação de transferência" é ambígua.** Uma única **Transferência**
  dispara DOIS eventos de notificação distintos, com destinatários e conteúdos
  diferentes: **Recebimento** (ao aluno) e **Confirmação de Envio** (ao
  professor). Sempre nomeie qual dos dois. O Lab04S01 exige ambos ("template
  para professor e template para aluno"); versões anteriores da especificação
  (FR-006, US1) mencionavam apenas o aluno e estão sendo reconciliadas.

## Example dialogue

> **Dev**: Quando o professor envia moedas, mando o e-mail pra ele também?
> **Domínio**: Sim. A Transferência dispara duas notificações: o Recebimento,
> que vai pro aluno, e a Confirmação de Envio, que vai pro professor.
> **Dev**: E o que muda no conteúdo?
> **Domínio**: O Recebimento mostra o novo saldo do aluno; a Confirmação de
> Envio mostra o saldo que sobrou pro professor. Mesmo Motivo nos dois.
> **Dev**: Isso aparece no Extrato também?
> **Domínio**: O Extrato é outra coisa — é a lista de movimentações. A
> notificação é o e-mail do evento; o Extrato é a consulta do histórico.
