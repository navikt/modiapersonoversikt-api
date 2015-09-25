package no.nav.sbl.dialogarena.varsel.service;

import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.*;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.domain.Varsel.VarselMelding;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.varsel.domain.Varsel.VARSLER_MED_STATUS_FERDIG;
import static no.nav.sbl.dialogarena.varsel.domain.Varsel.VarselMelding.VARSELMELDINGER_MED_KVITTERING_OK;

public class VarslerServiceImpl implements VarslerService {

    @Inject
    private VarslerPorttype ws;

    @Override
    public List<Varsel> hentAlleVarsler(String fnr) {
        WSHentVarslerResponse response = ws.hentVarsler(
            new WSHentVarslerRequest().withIdent(new WSFnr().withValue(fnr))
        );
        return on(response.getVarselListe().getVarsel()).map(TIL_VARSEL).filter(VARSLER_MED_STATUS_FERDIG).collect();
    }

    private static Transformer<WSVarsel, Varsel> TIL_VARSEL = new Transformer<WSVarsel, Varsel>() {
        @Override
        public Varsel transform(WSVarsel wsVarsel) {
            String varselType = wsVarsel.getVarseltype();
            DateTime mottattTidspunkt = optional(wsVarsel.getMottattidspunkt()).map(TIL_DATETIME).getOrElse(null);
            String status = wsVarsel.getStatus();
            List<VarselMelding> meldingListe = on(wsVarsel.getMeldingListe().getMelding())
                .map(TIL_VARSEL_MELDING)
                .filter(VARSELMELDINGER_MED_KVITTERING_OK)
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
