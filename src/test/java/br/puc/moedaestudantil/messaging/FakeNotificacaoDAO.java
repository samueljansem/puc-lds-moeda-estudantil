package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Fake do {@link NotificacaoDAO} pra testes unitários do drainer/listeners.
 * Mantém um {@code Map<id,Notificacao>} em memória; cobre apenas os métodos
 * exercitados pelos testes — o resto delega pra UnsupportedOperationException.
 */
class FakeNotificacaoDAO implements NotificacaoDAO {

    final Map<Long, Notificacao> byId = new LinkedHashMap<>();

    FakeNotificacaoDAO(List<Notificacao> seed) {
        for (Notificacao n : seed) byId.put(n.getId(), n);
    }

    @Override
    public List<Notificacao> findFirst50ByStatusOrderByCriadaEmAsc(StatusNotificacao status) {
        return byId.values().stream().filter(n -> n.getStatus() == status).limit(50).toList();
    }

    @Override
    public int marcarEnviada(Long id) {
        Notificacao n = byId.get(id);
        if (n == null || n.getStatus() != StatusNotificacao.PENDENTE) return 0;
        n.setStatus(StatusNotificacao.ENVIADA);
        return 1;
    }

    @Override
    public Optional<Notificacao> findById(Long id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Notificacao> findByDestinatarioOrderByCriadaEmDesc(String destinatario) {
        throw new UnsupportedOperationException("não usado");
    }

    @Override
    public List<Notificacao> listarOrdemDescrescente() {
        throw new UnsupportedOperationException("não usado");
    }

    // --- CrudRepository: stubs ---

    @Override public <S extends Notificacao> S save(S e) { byId.put(e.getId(), e); return e; }
    @Override public <S extends Notificacao> S update(S e) { byId.put(e.getId(), e); return e; }
    @Override public <S extends Notificacao> List<S> saveAll(Iterable<S> it) {
        List<S> out = new ArrayList<>(); for (S s : it) out.add(save(s)); return out;
    }
    @Override public <S extends Notificacao> List<S> updateAll(Iterable<S> it) {
        List<S> out = new ArrayList<>(); for (S s : it) out.add(update(s)); return out;
    }
    @Override public boolean existsById(Long id) { return byId.containsKey(id); }
    @Override public List<Notificacao> findAll() { return new ArrayList<>(byId.values()); }
    @Override public long count() { return byId.size(); }
    @Override public void deleteById(Long id) { byId.remove(id); }
    @Override public void delete(Notificacao n) { byId.remove(n.getId()); }
    @Override public void deleteAll(Iterable<? extends Notificacao> it) { it.forEach(this::delete); }
    @Override public void deleteAll() { byId.clear(); }
}
