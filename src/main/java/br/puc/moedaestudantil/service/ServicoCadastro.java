package br.puc.moedaestudantil.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dao.EmpresaParceiraDAO;
import br.puc.moedaestudantil.dao.InstituicaoDAO;
import br.puc.moedaestudantil.dao.UsuarioDAO;
import br.puc.moedaestudantil.dto.CadastroAlunoForm;
import br.puc.moedaestudantil.dto.CadastroEmpresaForm;
import br.puc.moedaestudantil.dto.EditaAlunoForm;
import br.puc.moedaestudantil.dto.EditaEmpresaForm;
import br.puc.moedaestudantil.exception.CadastroDuplicadoException;
import br.puc.moedaestudantil.exception.SenhaIncorretaException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.EmpresaParceira;
import br.puc.moedaestudantil.model.Instituicao;
import br.puc.moedaestudantil.model.TipoAtor;
import br.puc.moedaestudantil.model.Usuario;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class ServicoCadastro {

    private static final int BCRYPT_COST = 12;

    private final AlunoDAO alunoDAO;
    private final EmpresaParceiraDAO empresaDAO;
    private final CredencialDAO credencialDAO;
    private final InstituicaoDAO instituicaoDAO;
    private final UsuarioDAO usuarioDAO;

    public ServicoCadastro(AlunoDAO alunoDAO,
                           EmpresaParceiraDAO empresaDAO,
                           CredencialDAO credencialDAO,
                           InstituicaoDAO instituicaoDAO,
                           UsuarioDAO usuarioDAO) {
        this.alunoDAO = alunoDAO;
        this.empresaDAO = empresaDAO;
        this.credencialDAO = credencialDAO;
        this.instituicaoDAO = instituicaoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    @Transactional
    public Aluno cadastrarAluno(CadastroAlunoForm form) {
        if (alunoDAO.existsByCpf(form.cpf())) {
            throw new CadastroDuplicadoException("cpf");
        }
        if (alunoDAO.existsByEmail(form.email())) {
            throw new CadastroDuplicadoException("email");
        }
        if (credencialDAO.existsByLogin(form.login())) {
            throw new CadastroDuplicadoException("login");
        }

        Instituicao instituicao = instituicaoDAO.findById(form.instituicaoId())
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));

        Credencial credencial = new Credencial(form.login(), hash(form.senha()), TipoAtor.ALUNO);
        credencial = credencialDAO.save(credencial);

        Aluno aluno = new Aluno(
                form.nome(),
                form.email(),
                credencial,
                form.cpf(),
                form.rg(),
                form.endereco(),
                form.curso(),
                instituicao
        );
        return alunoDAO.save(aluno);
    }

    @Transactional
    public EmpresaParceira cadastrarEmpresa(CadastroEmpresaForm form) {
        if (empresaDAO.existsByCnpj(form.cnpj())) {
            throw new CadastroDuplicadoException("cnpj");
        }
        if (empresaDAO.existsByEmail(form.email())) {
            throw new CadastroDuplicadoException("email");
        }
        if (credencialDAO.existsByLogin(form.login())) {
            throw new CadastroDuplicadoException("login");
        }

        Credencial credencial = new Credencial(form.login(), hash(form.senha()), TipoAtor.EMPRESA_PARCEIRA);
        credencial = credencialDAO.save(credencial);

        EmpresaParceira empresa = new EmpresaParceira(
                form.nome(),
                form.email(),
                credencial,
                form.cnpj()
        );
        return empresaDAO.save(empresa);
    }

    @Transactional
    public Aluno atualizarAluno(Long id, EditaAlunoForm form) {
        Aluno aluno = alunoDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        if (!aluno.getEmail().equals(form.email()) && alunoDAO.existsByEmail(form.email())) {
            throw new CadastroDuplicadoException("email");
        }

        aluno.setEmail(form.email());
        aluno.setEndereco(form.endereco());
        aluno.setCurso(form.curso());
        return alunoDAO.update(aluno);
    }

    @Transactional
    public EmpresaParceira atualizarEmpresa(Long id, EditaEmpresaForm form) {
        EmpresaParceira empresa = empresaDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        if (!empresa.getEmail().equals(form.email()) && empresaDAO.existsByEmail(form.email())) {
            throw new CadastroDuplicadoException("email");
        }

        empresa.setNome(form.nome());
        empresa.setEmail(form.email());
        return empresaDAO.update(empresa);
    }

    @Transactional
    public void trocarSenha(Long credencialId, String senhaAtual, String senhaNova) {
        if (senhaNova == null || senhaNova.length() < 6) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
        }
        Credencial c = credencialDAO.findById(credencialId)
                .orElseThrow(() -> new IllegalStateException("Credencial não encontrada."));
        if (!BCrypt.verifyer().verify(senhaAtual.toCharArray(), c.getSenhaHash()).verified) {
            throw new SenhaIncorretaException();
        }
        c.setSenhaHash(hash(senhaNova));
        credencialDAO.update(c);
    }

    @Transactional
    public void desativarUsuario(Long usuarioId) {
        Usuario u = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado."));
        u.setAtivo(false);
        usuarioDAO.update(u);
    }

    private String hash(String senhaPlana) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, senhaPlana.toCharArray());
    }
}
