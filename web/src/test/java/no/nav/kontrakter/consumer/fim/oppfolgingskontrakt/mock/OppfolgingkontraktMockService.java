package no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.mock;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OppfolgingkontraktMockService implements OppfolgingskontraktServiceBi {

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
