package no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.mock;

import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.YtelseskontraktMapper;
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.YtelseskontraktService;
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain.YtelseskontraktRequest;
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain.YtelseskontraktResponse;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimBruker;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimRettighetsgruppe;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class YtelseskontraktMockService implements YtelseskontraktService, Serializable {

    private YtelseskontraktMapper mapper = null;

    @Override
    public YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request) {

        FimHentYtelseskontraktListeResponse rawResponse = new FimHentYtelseskontraktListeResponse();
        rawResponse.setBruker(new FimBruker().withRettighetsgruppe(new FimRettighetsgruppe().withRettighetsGruppe("test")));

        if (isBlank(request.getFodselsnummer())) {
            rawResponse.withYtelseskontraktListe(YtelseskontraktMockFactory.createYtelsesKontrakter());
        } else if (isNotBlank(request.getFodselsnummer()) && request.getFrom() != null && request.getTo() != null) {
            rawResponse.withYtelseskontraktListe(YtelseskontraktMockFactory.createYtelsesKontrakter(request.getFodselsnummer(), request.getFrom().toDate(), request.getTo().toDate()));
        } else {
            rawResponse.withYtelseskontraktListe(YtelseskontraktMockFactory.createYtelsesKontrakter(request.getFodselsnummer(), null, null));
        }

        return mapper.map(rawResponse);
    }

    public void setMapper(YtelseskontraktMapper mapper) {
        this.mapper = mapper;
    }
}
