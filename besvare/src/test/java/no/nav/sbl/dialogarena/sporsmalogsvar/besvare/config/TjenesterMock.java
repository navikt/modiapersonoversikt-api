package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerRequest;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.by;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.getFromBehandlingsresultat;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.joda.time.DateTime.now;

@Configuration
public class TjenesterMock {

    public static final String TRAAD = "1";
    private static final Logger LOG = LoggerFactory.getLogger(TjenesterMock.class);
    private static final String SPORSMAL = "SPORSMAL";
    private static final String SVAR = "SVAR";
    private static final String LANG_TEKST = "Lorem ipsum dolor sit amet, " +
            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
            "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
            " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis" +
            " eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis" +
            " in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus" +
            " dynamicus, qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit" +
            " litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes" +
            " in futurum.";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final PreparedIterable<WSHenvendelse> HENVENDELSER = on(asList(
            createWSHenvendelse(SPORSMAL, "Jeg lurer på noe!", now().minusWeeks(2)),
            createWSHenvendelse(SVAR, "Hva da?", now().minusWeeks(1)),
            createWSHenvendelse(SPORSMAL, "Jo nå skal du høre: " + LANG_TEKST, now().minusDays(5)),
            createWSHenvendelse(SVAR, "Ja det var ikke småtterier!", now().minusDays(4)),
            createWSHenvendelse(SPORSMAL, "Nei det kan du si. Dette er litt sensitivt, men, joda, så neida så, og ikke nok med det, " + LANG_TEKST, now().minusDays(2)).withSensitiv(true)));


    private static WSHenvendelse createWSHenvendelse(String type, String fritekst, DateTime opprettet) {
        WSHenvendelse wsHenvendelse = new WSHenvendelse()
            .withBehandlingsId(randomNumeric(5))
            .withHenvendelseType(type)
            .withOpprettetDato(opprettet)
            .withTraad(TRAAD);
        try {
            Map<String, String> fritekstMapping = new HashMap<>();
            fritekstMapping.put("fritekst", fritekst);
            wsHenvendelse.setBehandlingsresultat(MAPPER.writeValueAsString(fritekstMapping));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kunne ikke bygge JSON", e);
        }
        return wsHenvendelse;
    }


    @Bean
    public HenvendelsePortType henvendelsePortType() {
        class HenvendelseStub extends ItPings implements HenvendelsePortType {
            @Override
            public void merkMeldingSomLest(String id) {
                LOG.info("HenvendelsePortType: Henvendelse med id {} er lest.", id);
            }

            @Override
            public List<WSHenvendelse> hentHenvendelseListe(String fnr, List<String> strings) {
                LOG.info("Henter alle henvendelser for bruker med fødselsnummer " + fnr);
                return HENVENDELSER.collect();
            }
        }
        return new HenvendelseStub();
    }


    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        class BesvareHenvendelseStub extends ItPings implements BesvareHenvendelsePortType {

            @Override
            public void journalforMeldinger(List<WSMelding> meldinger) {
            }

            @Override
            public void besvarSporsmal(WSSvar wsSvar) {
                LOG.info("BesvareHenvendelsePortType besvarer spørsmål (svar behandlingId {} \"{}\")", wsSvar.getBehandlingsId(), abbreviate(wsSvar.getFritekst(), 30));
            }

            @Override
            public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
                WSHenvendelse nyesteHenvendelse = HENVENDELSER.collect(by(OPPRETTET_DATO).descending()).get(0);
                WSSporsmal sporsmal = new WSSporsmal()
                        .withFritekst(optional(nyesteHenvendelse.getBehandlingsresultat()).map(getFromBehandlingsresultat("fritekst")).getOrElse(null))
                        .withOpprettet(nyesteHenvendelse.getOpprettetDato())
                        .withBehandlingsId(nyesteHenvendelse.getBehandlingsId())
                        .withTema("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT")
                        .withTraad(TRAAD);
                return new WSSporsmalOgSvar().withSporsmal(sporsmal).withSvar(new WSSvar().withBehandlingsId(randomNumeric(5)));
            }

            @Override
            public HentSakerResponse hentSaker(HentSakerRequest parameters) {
                return new HentSakerResponse().withSaker(Arrays.asList(new WSSak().withGenerell(true).withOpprettetDato(DateTime.now()).withSakId("123").withStatus("Foobar")));
            }
        }
        return new BesvareHenvendelseStub();
    }

    private static final Transformer<WSHenvendelse, DateTime> OPPRETTET_DATO = new Transformer<WSHenvendelse, DateTime>() {
        @Override
        public DateTime transform(WSHenvendelse wshenvendelse) {
            return wshenvendelse.getOpprettetDato();
        }
    };


    abstract static class ItPings {
        public boolean ping() {
            return true;
        }
    }
}
