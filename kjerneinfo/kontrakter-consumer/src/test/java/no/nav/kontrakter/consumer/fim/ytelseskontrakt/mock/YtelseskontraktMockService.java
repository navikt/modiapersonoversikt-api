package no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock;

import no.nav.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimBruker;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimRettighetsgruppe;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class YtelseskontraktMockService implements YtelseskontraktServiceBi, Serializable {

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

        return mapper.map(rawResponse, YtelseskontraktResponse.class);
    }

    public void setMapper(YtelseskontraktMapper mapper) {
        this.mapper = mapper;
    }
}
