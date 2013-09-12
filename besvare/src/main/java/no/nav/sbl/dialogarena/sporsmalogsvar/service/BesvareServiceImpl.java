package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class BesvareServiceImpl implements BesvareService {

    @Inject
    BesvareHenvendelsePortType besvareHenvendelsePortType;
    @Inject
    HenvendelsePortType henvendelsePortType;

    public void besvareSporsmal(Svar svar) {
        besvareHenvendelsePortType.besvarSporsmal(TIL_WSSVAR.transform(svar));
    }

    public BesvareSporsmalDetaljer hentDetaljer(String fnr, String oppgaveId) {
        WSSporsmalOgSvar wsSporsmalOgSvar = besvareHenvendelsePortType.hentSporsmalOgSvar(oppgaveId);

        BesvareSporsmalDetaljer besvareSporsmalDetaljer = new BesvareSporsmalDetaljer();
        besvareSporsmalDetaljer.tema = wsSporsmalOgSvar.getSporsmal().getTema();
        besvareSporsmalDetaljer.svar = TIL_SVAR.transform(wsSporsmalOgSvar.getSvar());
        besvareSporsmalDetaljer.sporsmal = TIL_SPORSMAL.transform(wsSporsmalOgSvar.getSporsmal());

        List<WSHenvendelse> wsHenvendelser = on(henvendelsePortType.hentHenvendelseListe(fnr)).collect(NYESTE_FORST);

        besvareSporsmalDetaljer.tildligereDialog = on(wsHenvendelser)
                .filter(where(TRAAD_ID, equalTo(wsSporsmalOgSvar.getSporsmal().getTraad())))
                .filter(where(BEHANDLINGS_ID, not(equalTo(wsSporsmalOgSvar.getSporsmal().getBehandlingsId()))))
                .map(TIL_HENVENDELSE).collect();

        return besvareSporsmalDetaljer;

    }

    private static final Transformer<Svar, WSSvar> TIL_WSSVAR = new Transformer<Svar, WSSvar>() {
        @Override
        public WSSvar transform(Svar svar) {
            return new WSSvar().withBehandlingsId(svar.getBehandlingId()).withTema(svar.tema).withFritekst(svar.fritekst).withSensitiv(svar.sensitive);
        }
    };

    private static final Comparator<WSHenvendelse> NYESTE_FORST = new Comparator<WSHenvendelse>() {
        @Override
        public int compare(WSHenvendelse o1, WSHenvendelse o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

    private static final Transformer<WSHenvendelse, String> TRAAD_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getTraad();
        }
    };

    private static final Transformer<WSHenvendelse, String> BEHANDLINGS_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getBehandlingsId();
        }
    };

    private static final Transformer<WSHenvendelse, Henvendelse> TIL_HENVENDELSE = new Transformer<WSHenvendelse, Henvendelse>() {
        @Override
        public Henvendelse transform(WSHenvendelse wsHenvendelse) {
            Henvendelse henvendelse = new Henvendelse();
            henvendelse.sendtDato = formatertDato(wsHenvendelse.getOpprettetDato());
            String type = wsHenvendelse.getHenvendelseType();

            switch (type) {
                case "SPORSMAL":
                    henvendelse.overskrift = "Fra bruker";
                    break;
                case "SVAR":
                    henvendelse.overskrift = "Fra NAV";
                    break;
                default:
                    throw new RuntimeException("Skjønner ikke hva " + type + " er!");
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> behandlingsresultat;
            try {
                behandlingsresultat = mapper.readValue(wsHenvendelse.getBehandlingsresultat(), Map.class);
            } catch (IOException e) {
                throw new RuntimeException("Kunne ikke lese ut behandlingsresultat", e);
            }
            henvendelse.fritekst = behandlingsresultat.get("fritekst");

            return henvendelse;
        }
    };

    private static final Transformer<WSSvar, Svar> TIL_SVAR = new Transformer<WSSvar, Svar>() {
        @Override
        public Svar transform(WSSvar wsSvar) {
            Svar svar = new Svar(wsSvar.getBehandlingsId());
            svar.tema = wsSvar.getTema();
            svar.fritekst = wsSvar.getFritekst();
            svar.sensitive = wsSvar.isSensitiv();
            return svar;
        }
    };

    private static final Transformer<WSSporsmal, Sporsmal> TIL_SPORSMAL = new Transformer<WSSporsmal, Sporsmal>() {
        @Override
        public Sporsmal transform(WSSporsmal wsSporsmal) {
            Sporsmal sporsmal = new Sporsmal();
            sporsmal.sendtDato = formatertDato(wsSporsmal.getOpprettet());
            sporsmal.fritekst = wsSporsmal.getFritekst();
            return sporsmal;
        }
    };

    private static String formatertDato(DateTime dato) {
        return DateTimeFormat.forPattern("EEEEE dd.MM.yyyy 'kl' HH:mm").withLocale(new Locale("nb")).print(dato);
    }
}