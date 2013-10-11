package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.asList;

@Configuration
public class TjenesterMock {

    private static final Logger LOG = LoggerFactory.getLogger(TjenesterMock.class);
    private static final String TRAAD = "1";

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return new HenvendelsePortType() {

            private static final String SPORSMAL = "SPORSMAL";
            private static final String SVAR = "SVAR";
            List<WSHenvendelse> henvendelser = asList(
                    createWSHenvendelse(SPORSMAL, DateTime.now().minusWeeks(2)),
                    createWSHenvendelse(SVAR, DateTime.now().minusWeeks(1)),
                    createWSHenvendelse(SPORSMAL, DateTime.now().minusDays(5)),
                    createWSHenvendelse(SVAR, DateTime.now().minusDays(4)),
                    createWSHenvendelse(SPORSMAL, DateTime.now().minusDays(2)));

            @Override
            public void merkMeldingSomLest(String id) {
                LOG.info("Henvendelse med id " + id + " er lest.");
            }

            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public List<WSHenvendelse> hentHenvendelseListe(String fnr, List<String> strings) {
                LOG.info("Henter alle henvendelser for bruker med fødselsnummer " + fnr);
                return henvendelser;                }

            WSHenvendelse createWSHenvendelse(String type, DateTime opprettet) {
                WSHenvendelse wsHenvendelse =
                        new WSHenvendelse().withBehandlingsId("" + new Random().nextInt()).withHenvendelseType(type)
                                .withOpprettetDato(opprettet).withTraad(TRAAD);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Map<String, String> fritekstMapping = new HashMap<>();
                    fritekstMapping.put("fritekst", "Lorem ipsum dolor sit amet, " +
                            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
                            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
                            "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                            " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis" +
                            " eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis" +
                            " in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus" +
                            " dynamicus, qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit" +
                            " litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes" +
                            " in futurum.");
                    wsHenvendelse.setBehandlingsresultat(mapper.writeValueAsString(fritekstMapping));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Kunne ikke bygge JSON", e);
                }
                return wsHenvendelse;
            }
        };
    }

    @Bean
    BesvareHenvendelsePortType besvareHenvendelsePortType() {
        return new BesvareHenvendelsePortType() {

            @Override
            public void besvarSporsmal(WSSvar wsSvar) {
            }

            @Override
            public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
                Random random = new Random();
                WSSporsmal spsm = new WSSporsmal()
                        .withFritekst("Lorem ipsum dolor sit amet, " +
                                "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
                                "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
                                "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto")
                        .withOpprettet(DateTime.now())
                        .withTema("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT")
                        .withTraad(TRAAD);
                WSSvar svar = new WSSvar().withBehandlingsId("" + random.nextInt());
                return new WSSporsmalOgSvar().withSporsmal(spsm).withSvar(svar);
            }

            @Override
            public boolean ping() {
                return true;
            }
        };
    }
}
