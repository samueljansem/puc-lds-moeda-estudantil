# Transferência de moedas restrita à instituição do professor

A especificação (release-02) não diz explicitamente que um professor só pode
enviar moedas a alunos da sua própria instituição, mas a tela de transferência
sempre listou apenas alunos da mesma instituição (US10) e o modelo de domínio
trata o professor como alguém que "reconhece **seus** alunos". Decidimos
**impor essa regra também no serviço** (`ServicoMoeda.transferir`), rejeitando
transferências para alunos de outra instituição ou inativos — não só na UI.

**Por quê:** a tela é conveniência; o serviço é a fronteira de confiança. Sem a
verificação no serviço, um `POST` forjado com qualquer `alunoId` transferiria
moedas para qualquer aluno do sistema, contornando o escopo institucional. A
regra é barata, alinha código e modelo, e fecha esse furo. A alternativa
(confiar apenas na UI) foi rejeitada por deixar a invariante sem garantia.
