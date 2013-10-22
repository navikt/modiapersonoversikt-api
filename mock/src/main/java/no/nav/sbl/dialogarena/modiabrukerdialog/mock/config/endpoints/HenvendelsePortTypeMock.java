package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Arrays.asList;

@Configuration
public class HenvendelsePortTypeMock {

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        final Integer traadId = 1;
        return new HenvendelsePortType() {

            private static final String SPORSMAL = "SPORSMAL";
            private static final String SVAR = "SVAR";
            List<WSHenvendelse> henvendelser = asList(
                    createWSHenvendelse(SPORSMAL, "" + traadId, "ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", DateTime.now().minusWeeks(2)),
                    createWSHenvendelse(SPORSMAL, "" + (traadId + 1), "INTERNASJONALT", DateTime.now().minusWeeks(1)),
                    createWSHenvendelse(SVAR, "" + (traadId + 1), "INTERNASJONALT", DateTime.now().minusDays(5), DateTime.now().minusDays(4)),
                    createWSHenvendelse(SPORSMAL, "" + (traadId + 2), "HJELPEMIDLER", DateTime.now().minusDays(3)),
                    createWSHenvendelse(SVAR, "" + (traadId + 2), "HJELPEMIDLER", DateTime.now().minusHours(5), null),
                    createWSHenvendelse(SPORSMAL, "" + (traadId + 3), "HJELPEMIDLER", DateTime.now().minusHours(10)));

            WSHenvendelse createWSHenvendelse(String type, String traad, String tema, DateTime opprettet) {
                return createWSHenvendelse(type, traad, tema, opprettet, opprettet);
            }

            WSHenvendelse createWSHenvendelse(String type, String traad, String tema, DateTime opprettet, DateTime lestdato) {
                Random random = new Random();
                WSHenvendelse wsHenvendelse =
                        new WSHenvendelse().withBehandlingsId("" + random.nextInt()).withHenvendelseType(type)
                                .withOpprettetDato(opprettet).withTraad(traad).withTema(tema.toString()).withLestDato(lestdato);
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

            @Override
            public void merkMeldingSomLest(@WebParam(name = "behandlingsId", targetNamespace = "") String behandlingsId) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public List<WSHenvendelse> hentHenvendelseListe(@WebParam(name = "fodselsnummer", targetNamespace = "") String fodselsnummer, @WebParam(name = "henvendelseType", targetNamespace = "") List<String> henvendelseType) {
                return henvendelser;
            }
        };
    }

}
