package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume;

import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;

public class Transform {

    public static final Transformer<Melding, WSSvar> tilWsSvar(final String tema, final boolean sensitiv) { return new Transformer<Melding, WSSvar>() {
        @Override public WSSvar transform(Melding svar) {
            return new WSSvar().withBehandlingsId(svar.behandlingId).withTema(tema).withFritekst(svar.fritekst).withSensitiv(sensitiv);
        }
    }; }

    public static final Transformer<WSHenvendelse, String> TRAAD_ID = new Transformer<WSHenvendelse, String>() {
        @Override public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getTraad();
        }
    };

    public static final Transformer<WSHenvendelse, String> BEHANDLINGSID = new Transformer<WSHenvendelse, String>() {
        @Override public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getBehandlingsId();
        }
    };

    public static final Transformer<WSHenvendelse, Boolean> SENSITIV = new Transformer<WSHenvendelse, Boolean>() {
        @Override public Boolean transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.isSensitiv();
        }
    };


    public static final Transformer<WSHenvendelse, Melding> TIL_MELDING = new Transformer<WSHenvendelse, Melding>() {
        @Override public Melding transform(WSHenvendelse wsHenvendelse) {
            String fritekst = wsHenvendelse.getBehandlingsresultat();
            return new Melding(wsHenvendelse.getBehandlingsId(), "SPORSMAL".equals(wsHenvendelse.getHenvendelseType()) ? INNGAENDE : UTGAENDE, wsHenvendelse.getOpprettetDato(), fritekst);
        }
    };
}
