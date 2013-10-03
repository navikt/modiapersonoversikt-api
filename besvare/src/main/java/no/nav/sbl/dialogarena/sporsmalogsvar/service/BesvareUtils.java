package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.Session;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

public class BesvareUtils {

    public static final Transformer<Svar, WSSvar> TIL_WSSVAR = new Transformer<Svar, WSSvar>() {
        @Override
        public WSSvar transform(Svar svar) {
            return new WSSvar().withBehandlingsId(svar.getBehandlingId()).withTema(svar.tema).withFritekst(svar.fritekst).withSensitiv(svar.sensitiv);
        }
    };

    public static final Comparator<WSHenvendelse> NYESTE_FORST = new Comparator<WSHenvendelse>() {
        @Override
        public int compare(WSHenvendelse o1, WSHenvendelse o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

    public static final Transformer<WSHenvendelse, String> TRAAD_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getTraad();
        }
    };

    public static final Transformer<WSHenvendelse, String> BEHANDLINGS_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getBehandlingsId();
        }
    };

    public static final Transformer<WSHenvendelse, Henvendelse> TIL_HENVENDELSE = new Transformer<WSHenvendelse, Henvendelse>() {
        @Override
        public Henvendelse transform(WSHenvendelse wsHenvendelse) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> behandlingsresultat;
            try {
                behandlingsresultat = mapper.readValue(wsHenvendelse.getBehandlingsresultat(), Map.class);
            } catch (IOException e) {
                throw new RuntimeException("Kunne ikke lese ut behandlingsresultat", e);
            }
            String fritekst = behandlingsresultat.get("fritekst");
            return new Henvendelse(Henvendelsestype.valueOf(wsHenvendelse.getHenvendelseType()), wsHenvendelse.getOpprettetDato(), fritekst);
        }
    };

    public static final Transformer<WSSvar, Svar> TIL_SVAR = new Transformer<WSSvar, Svar>() {
        @Override
        public Svar transform(WSSvar wsSvar) {
            Svar svar = new Svar(wsSvar.getBehandlingsId());
            svar.tema = wsSvar.getTema();
            svar.fritekst = wsSvar.getFritekst();
            svar.sensitiv = wsSvar.isSensitiv();
            return svar;
        }
    };

    public static final Transformer<WSSporsmal, Sporsmal> TIL_SPORSMAL = new Transformer<WSSporsmal, Sporsmal>() {
        @Override
        public Sporsmal transform(WSSporsmal wsSporsmal) {
            return new Sporsmal(wsSporsmal.getFritekst(), wsSporsmal.getOpprettet());
        }
    };

    public static String formatertDato(DateTime dato) {
        return DateTimeFormat.forPattern("EEEEE dd.MM.yyyy 'kl' HH:mm").withLocale(Session.get().getLocale()).print(dato);
    }
}
