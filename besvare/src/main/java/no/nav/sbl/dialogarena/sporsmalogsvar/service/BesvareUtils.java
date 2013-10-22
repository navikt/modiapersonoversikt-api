package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;

public class BesvareUtils {

    public static final Transformer<Svar, WSSvar> TIL_WSSVAR = new Transformer<Svar, WSSvar>() {
        @Override
        public WSSvar transform(Svar svar) {
            return new WSSvar().withBehandlingsId(svar.behandlingId).withTema(svar.tema).withFritekst(svar.fritekst).withSensitiv(svar.sensitiv);
        }
    };



    



    public static final Transformer<WSHenvendelse, Henvendelse> TIL_HENVENDELSE = new Transformer<WSHenvendelse, Henvendelse>() {
        final ObjectMapper mapper = new ObjectMapper();
        @Override
        public Henvendelse transform(WSHenvendelse wsHenvendelse) {
            Map<String, String> behandlingsresultat;
            try {
                behandlingsresultat = mapper.readValue(wsHenvendelse.getBehandlingsresultat(), new TypeReference<Map<String, String>>() { });
            } catch (IOException e) {
                throw new RuntimeException("Kunne ikke lese ut behandlingsresultat", e);
            }
            String fritekst = behandlingsresultat.get("fritekst");
            return new Henvendelse("SPORSMAL".equals(wsHenvendelse.getHenvendelseType()) ? INNGAENDE : UTGAENDE, wsHenvendelse.getOpprettetDato(), fritekst);
        }
    };

    public static final Transformer<WSSvar, Svar> TIL_SVAR = new Transformer<WSSvar, Svar>() {
        @Override
        public Svar transform(WSSvar wsSvar) {
            return new Svar(wsSvar.getBehandlingsId(), wsSvar.getTema(), wsSvar.getFritekst(), wsSvar.isSensitiv());
        }
    };

    public static final Transformer<WSSporsmal, Sporsmal> TIL_SPORSMAL = new Transformer<WSSporsmal, Sporsmal>() {
        @Override
        public Sporsmal transform(WSSporsmal wsSporsmal) {
            return new Sporsmal(wsSporsmal.getFritekst(), wsSporsmal.getOpprettet());
        }
    };


}
