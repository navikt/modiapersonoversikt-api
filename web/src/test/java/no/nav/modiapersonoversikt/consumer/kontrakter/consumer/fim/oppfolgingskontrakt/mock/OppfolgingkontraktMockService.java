package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.mock;

import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktResponse;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktMapper;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OppfolgingkontraktMockService implements OppfolgingskontraktService {

    private OppfolgingskontraktMapper mapper = null;

    @Override
    public OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request) {

        WSHentOppfoelgingskontraktListeResponse rawResponse = new WSHentOppfoelgingskontraktListeResponse();

        if (isBlank(request.getFodselsnummer())) {
            rawResponse.getOppfoelgingskontraktListe().addAll(OppfolgingkontraktMockFactory.createOppfoelgingskontrakter(null, null, null));
            rawResponse.getOppfoelgingskontraktListe().add(OppfolgingkontraktMockFactory.createSYFOkontrakt(null, OppfolgingkontraktMockFactory.FAGSAKSTATUS_INAKTIV, null, null));
        } else if (isNotBlank(request.getFodselsnummer()) && request.getFrom() != null && request.getTo() != null) {
            String fnr = request.getFodselsnummer();
            Date fra = request.getFrom().toDate();
            Date til = request.getTo().toDate();
            rawResponse.getOppfoelgingskontraktListe().addAll(OppfolgingkontraktMockFactory.createOppfoelgingskontrakter(fnr, fra, til));
        }

        return mapper.map(rawResponse);
    }

    public void setMapper(OppfolgingskontraktMapper mapper) {
        this.mapper = mapper;
    }

}
