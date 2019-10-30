package no.nav.kjerneinfo.consumer.fim.person.support;

import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kjerneinfo.consumer.mdc.MDCUtils;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.tjeneste.virksomhet.person.v3.HentSikkerhetstiltakPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSPersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSSikkerhetstiltak;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentSikkerhetstiltakRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentSikkerhetstiltakResponse;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class HentSikkerhetstiltakService {

    private static final Logger logger = LoggerFactory.getLogger(HentSikkerhetstiltakService.class);

    private final PersonV3 service;

    HentSikkerhetstiltakService(PersonV3 service) {
        this.service = service;
    }

    protected Sikkerhetstiltak hentSikkerhetstiltak(String ident) {
        MDCUtils.putMDCInfo("hentSikkerhetstiltak()", "Personidentifikator:" + ident);
        logger.info("Henter ut eventuell sikkerhetstiltak om bruker med personidentifikator {}", ident);

        WSHentSikkerhetstiltakResponse wsResponse;
        WSHentSikkerhetstiltakRequest wsRequest = new WSHentSikkerhetstiltakRequest()
                .withAktoer(new WSPersonIdent().withIdent(new WSNorskIdent().withIdent(ident)));
        try {
            wsResponse = service.hentSikkerhetstiltak(wsRequest);
        } catch (HentSikkerhetstiltakPersonIkkeFunnet hentSikkerhetstiltakPersonIkkeFunnet) {
            logger.info("bruker med id {} ikke funnet.", ident);
            return new Sikkerhetstiltak();
        }

        if(wsResponse == null || wsResponse.getSikkerhetstiltak() == null) return new Sikkerhetstiltak();

        return mapSikkerhetstiltakFraWs.apply(wsResponse.getSikkerhetstiltak());
    }

    private Function<WSSikkerhetstiltak, Sikkerhetstiltak> mapSikkerhetstiltakFraWs =
            new Function<WSSikkerhetstiltak, Sikkerhetstiltak>() {
        @Override
        public Sikkerhetstiltak apply(WSSikkerhetstiltak ws) {
            Sikkerhetstiltak s = new Sikkerhetstiltak();
            s.setSikkerhetstiltaksbeskrivelse(ws.getSikkerhetstiltaksbeskrivelse());
            s.setSikkerhetstiltakskode(ws.getSikkerhetstiltakskode());
            if(ws.getPeriode() != null) {
                s.setPeriode(mapPeriodeFraWs.apply(ws.getPeriode()));
            }
            return s;
        }
    };

    private Function<WSPeriode, Periode> mapPeriodeFraWs = ws -> {
        Periode p = new Periode();
        if(ws.getFom() != null) {
            p.setFrom(new LocalDate(ws.getFom().toGregorianCalendar().getTimeInMillis()));
        }
        if(ws.getTom() != null) {
            p.setTo(new LocalDate(ws.getTom().toGregorianCalendar().getTimeInMillis()));
        }
        return p;
    };
}
