package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.VantagemDAO;
import br.puc.moedaestudantil.model.Vantagem;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/vantagens")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class FotoVantagemController {

    private final VantagemDAO vantagemDAO;

    public FotoVantagemController(VantagemDAO vantagemDAO) {
        this.vantagemDAO = vantagemDAO;
    }

    @Get("/{id}/foto")
    public MutableHttpResponse<?> foto(@PathVariable Long id) {
        Vantagem v = vantagemDAO.findById(id).orElse(null);
        if (v == null || !v.temFoto()) {
            return HttpResponse.notFound();
        }
        String ct = v.getFotoContentType() != null ? v.getFotoContentType() : MediaType.IMAGE_JPEG;
        return HttpResponse.ok(v.getFoto()).contentType(ct);
    }
}
