package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume;

import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad.Journalforingkvittering;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import org.apache.commons.collections15.Transformer;

public class Transform {

    public static final Transformer<WSMelding, Traad.Journalforingkvittering> JOURNALFORING = new Transformer<WSMelding, Traad.Journalforingkvittering>() {
        @Override
        public Journalforingkvittering transform(WSMelding melding) {
            if (melding.getJournalfortDato() != null) {
                return new Journalforingkvittering(melding.getJournalfortDato(), melding.getJournalfortSaksId(), melding.getJournalfortTema());
            }
            return null;
        }
    };


    public static final Transformer<Melding, WSSvar> tilWsSvar(final String tema, final boolean sensitiv) { return new Transformer<Melding, WSSvar>() {
        @Override public WSSvar transform(Melding svar) {
            return new WSSvar().withBehandlingsId(svar.behandlingId).withTema(tema).withFritekst(svar.fritekst).withSensitiv(sensitiv);
        }
    }; }

    public static final Transformer<WSMelding, String> TRAAD_ID = new Transformer<WSMelding, String>() {
        @Override public String transform(WSMelding wsMelding) {
            return wsMelding.getTraad();
        }
    };

    public static final Transformer<WSMelding, String> BEHANDLINGSID = new Transformer<WSMelding, String>() {
        @Override public String transform(WSMelding wsMelding) {
            return wsMelding.getBehandlingsId();
        }
    };

    public static final Transformer<WSMelding, Boolean> SENSITIV = new Transformer<WSMelding, Boolean>() {
        @Override public Boolean transform(WSMelding wsMelding) {
            return wsMelding.isSensitiv();
        }
    };


    public static final Transformer<WSMelding, Melding> TIL_MELDING = new Transformer<WSMelding, Melding>() {
        @Override public Melding transform(WSMelding wsMelding) {
            String fritekst = wsMelding.getTekst();
            return new Melding(wsMelding.getBehandlingsId(), Meldingstype.valueOf(wsMelding.getMeldingsType().toString()), wsMelding.getOpprettetDato(), fritekst);
        }
    };
}
