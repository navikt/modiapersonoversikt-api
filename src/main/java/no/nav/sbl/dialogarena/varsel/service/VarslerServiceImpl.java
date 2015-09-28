package no.nav.sbl.dialogarena.varsel.service;

import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.*;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.domain.Varsel.*;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.*;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.varsel.domain.Varsel.*;
import static no.nav.sbl.dialogarena.varsel.domain.Varsel.VarselMelding.STATUSKODE;
import static no.nav.sbl.dialogarena.varsel.domain.Varsel.VarselMelding.UTSENDINGSTIDSPUNKT;
import static org.slf4j.LoggerFactory.getLogger;

public class VarslerServiceImpl implements VarslerService {

    private static final Logger log = getLogger(VarslerServiceImpl.class);

    @Inject
    private VarslerPorttype ws;

    @Override
    public List<Varsel> hentAlleVarsler(String fnr) {
        try {
            WSHentVarslerResponse response = ws.hentVarsler(
                    new WSHentVarslerRequest().withIdent(new WSFnr().withValue(fnr))
            );

            return on(response.getVarselListe().getVarsel())
                    .map(TIL_VARSEL)
                    .filter(where(STATUS, equalTo(STATUS_FERDIG)))
                    .filter(where(MELDINGLISTE, not(empty())))
                    .collect();
        } catch (SOAPFaultException sfe) {
            log.error("Feilet ved uthenting av varsler.", sfe);
            return emptyList();
        }
    }

    private static Transformer<WSVarsel, Varsel> TIL_VARSEL = new Transformer<WSVarsel, Varsel>() {
        @Override
        public Varsel transform(WSVarsel wsVarsel) {
            String varselType = wsVarsel.getVarseltype();
            DateTime mottattTidspunkt = optional(wsVarsel.getMottattidspunkt()).map(TIL_DATETIME).getOrElse(null);
            String status = wsVarsel.getStatus();
            List<VarselMelding> meldingListe = on(wsVarsel.getMeldingListe().getMelding())
                    .map(TIL_VARSEL_MELDING)
                    .filter(where(UTSENDINGSTIDSPUNKT, not(equalTo(null))))
                    .filter(where(STATUSKODE, equalTo(STATUSKODE_OK)))
                    .collect();

            return new Varsel(varselType, mottattTidspunkt, status, meldingListe);
        }
    };

    private static final Transformer<WSMelding, VarselMelding> TIL_VARSEL_MELDING = new Transformer<WSMelding, VarselMelding>() {
        @Override
        public VarselMelding transform(WSMelding wsMelding) {
            String kanal = wsMelding.getKanal();
            String innhold = wsMelding.getInnhold();
            String mottakerInformasjon = wsMelding.getMottakerinformasjon();
            String statusKode = wsMelding.getStatuskode();
            DateTime utsendingsTidpunkt = optional(wsMelding.getUtsendingstidspunkt()).map(TIL_DATETIME).getOrElse(null);
            String feilbeskrivelse = wsMelding.getFeilbeskrivelse();
            String epostemne = wsMelding.getEpostemne();
            String url = wsMelding.getUrl();

            return new VarselMelding(kanal, innhold, mottakerInformasjon, utsendingsTidpunkt, statusKode, feilbeskrivelse, epostemne, url);
        }
    };

    private static final Transformer<XMLGregorianCalendar, DateTime> TIL_DATETIME = new Transformer<XMLGregorianCalendar, DateTime>() {
        @Override
        public DateTime transform(XMLGregorianCalendar xmlGregorianCalendar) {
            return new DateTime(xmlGregorianCalendar.toGregorianCalendar().getTime());
        }
    };
}
