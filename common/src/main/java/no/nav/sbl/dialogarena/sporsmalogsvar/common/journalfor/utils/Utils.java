package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Sak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding;
import org.apache.commons.collections15.Transformer;

import java.util.Comparator;

public class Utils {

    public static final Transformer<Sak, String> ARKIVTEMA = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.temakode;
        }
    };

    public static final Transformer<Melding, WSMelding> TIL_WSMELDING = new Transformer<Melding, WSMelding>() {
        @Override
        public WSMelding transform(Melding melding) {
            return new WSMelding()
                    .withBehandlingsId(melding.behandlingId)
                    .withMeldingstype(melding.avsender)
                    .withOpprettetDato(melding.sendtDato)
                    .withFritekst(melding.fritekst);
        }
    };

    public static final Comparator<Sak> SORTER_NYESTE_OVERST = new Comparator<Sak>() {
        @Override
        public int compare(Sak o1, Sak o2) {
            return o2.opprettetDato.compareTo(o1.opprettetDato);
        }
    };
}
