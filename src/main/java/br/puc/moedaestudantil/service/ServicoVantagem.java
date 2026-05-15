package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.dao.VantagemDAO;
import br.puc.moedaestudantil.exception.VantagemIndisponivelException;
import br.puc.moedaestudantil.model.EmpresaParceira;
import br.puc.moedaestudantil.model.Vantagem;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Singleton
public class ServicoVantagem {

    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/jpg"
    );
    private static final int TAMANHO_MAXIMO_FOTO = 2 * 1024 * 1024;

    private final VantagemDAO vantagemDAO;

    public ServicoVantagem(VantagemDAO vantagemDAO) {
        this.vantagemDAO = vantagemDAO;
    }

    @Transactional
    public Vantagem cadastrar(EmpresaParceira empresa,
                              String descricao,
                              int custo,
                              byte[] foto,
                              String fotoContentType) {
        validarDescricao(descricao);
        validarCusto(custo);
        validarFoto(foto, fotoContentType);

        Vantagem v = new Vantagem(empresa, descricao.trim(), custo, LocalDateTime.now());
        if (foto != null && foto.length > 0) {
            v.setFoto(foto);
            v.setFotoContentType(fotoContentType);
        }
        return vantagemDAO.save(v);
    }

    @Transactional
    public Vantagem atualizar(Long vantagemId,
                              EmpresaParceira empresa,
                              String descricao,
                              int custo,
                              byte[] foto,
                              String fotoContentType) {
        Vantagem v = vantagemDAO.findById(vantagemId)
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada."));
        if (!v.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalStateException("Você não tem permissão para editar esta vantagem.");
        }
        validarDescricao(descricao);
        validarCusto(custo);

        v.setDescricao(descricao.trim());
        v.setCusto(custo);
        if (foto != null && foto.length > 0) {
            validarFoto(foto, fotoContentType);
            v.setFoto(foto);
            v.setFotoContentType(fotoContentType);
        }
        return vantagemDAO.update(v);
    }

    @Transactional
    public void desativar(Long vantagemId, EmpresaParceira empresa) {
        Vantagem v = vantagemDAO.findById(vantagemId)
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada."));
        if (!v.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalStateException("Você não tem permissão para desativar esta vantagem.");
        }
        v.setAtiva(false);
        vantagemDAO.update(v);
    }

    @Transactional
    public void ativar(Long vantagemId, EmpresaParceira empresa) {
        Vantagem v = vantagemDAO.findById(vantagemId)
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada."));
        if (!v.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalStateException("Você não tem permissão para ativar esta vantagem.");
        }
        v.setAtiva(true);
        vantagemDAO.update(v);
    }

    public List<Vantagem> listarDaEmpresa(EmpresaParceira empresa) {
        return vantagemDAO.findByEmpresaIdOrderByCriadaEmDesc(empresa.getId());
    }

    public List<Vantagem> filtrarCatalogo(Integer custoMaximo, Long empresaId) {
        return vantagemDAO.filtrar(custoMaximo, empresaId);
    }

    public Vantagem buscarOuFalhar(Long id) {
        return vantagemDAO.findById(id)
                .orElseThrow(() -> new VantagemIndisponivelException("Vantagem não encontrada."));
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }
        if (descricao.length() > 500) {
            throw new IllegalArgumentException("Descrição deve ter no máximo 500 caracteres.");
        }
    }

    private void validarCusto(int custo) {
        if (custo <= 0) {
            throw new IllegalArgumentException("Custo deve ser de pelo menos 1 moeda.");
        }
    }

    private void validarFoto(byte[] foto, String contentType) {
        if (foto == null || foto.length == 0) return;
        if (foto.length > TAMANHO_MAXIMO_FOTO) {
            throw new IllegalArgumentException("Foto deve ter no máximo 2 MB.");
        }
        if (contentType == null || !CONTENT_TYPES_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Foto deve ser JPEG ou PNG.");
        }
    }
}
