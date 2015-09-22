package no.nav.sbl.dialogarena.varsel.service;

import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.*;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.domain.Varsel.VarselMelding;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;

public class VarslerServiceImpl implements VarslerService {

    @Inject
    private VarslerPorttype ws;

    @Override
    public List<Varsel> hentAlleVarsler(String fnr) {
        WSHentVarslerResponse response = ws.hentVarsler(
                new WSHentVarslerRequest().withIdent(new WSFnr().withValue(fnr))
        );
        return on(response.getVarselListe().getVarsel()).map(TIL_VARSEL).collect();
    }


    private static Transformer<WSVarsel, Varsel> TIL_VARSEL = new Transformer<WSVarsel, Varsel>() {
        @Override
        public Varsel transform(WSVarsel wsVarsel) {
            String varselType = wsVarsel.getVarseltype();
            DateTime mottattTidspunkt = new DateTime(
                    wsVarsel.getMottattidspunkt().toGregorianCalendar().getTime()
            );
            String statusKode = wsVarsel.getStatuskode();
            List<VarselMelding> meldingListe = on(wsVarsel.getMeldingListe().getMelding())
                    .map(TIL_VARSEL_MELDING)
                    .collect();

            return new Varsel(varselType, mottattTidspunkt, statusKode, meldingListe);
        }
    };

    private static final Transformer<WSMelding, VarselMelding> TIL_VARSEL_MELDING = new Transformer<WSMelding, VarselMelding>() {
        @Override
        public VarselMelding transform(WSMelding wsMelding) {
            String kanal = wsMelding.getKanal();
            String innhold = wsMelding.getInnhold();
            String mottakerInformasjon = wsMelding.getMottakerinformasjon();
            DateTime utsendingsTidpunkt = new DateTime(
                    wsMelding.getUtsendingstidspunkt().toGregorianCalendar().getTime()
            );
            String feilbeskrivelse = wsMelding.getFeilbeskrivelse();

            return new VarselMelding(kanal, innhold, mottakerInformasjon, utsendingsTidpunkt, feilbeskrivelse);
        }
    };
}
