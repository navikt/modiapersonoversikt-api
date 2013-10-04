package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.Tema;
import no.nav.sbl.dialogarena.sporsmalogsvar.mock.BesvareHenvendelsePortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.Utils.konfigurerMedHttps;

@Configuration
public class HenvendelseinnsynConfig {

    private static final Logger LOG = LoggerFactory.getLogger(HenvendelseinnsynConfig.class);

    @Profile({"default", "henvendelseinnsynDefault"})
    @Configuration
    public static class Default {

        @Value("${henvendelseendpoint.url}")
        protected String henvendelseEndpoint;

        @Bean
        public HenvendelsePortType henvendelsePortType() {
            JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
            Map<String, Object> properties = new HashMap<>();
            properties.put("schema-validation-enabled", true);
            properties.put(SecurityConstants.MUST_UNDERSTAND, false);
            factoryBean.setProperties(properties);
            factoryBean.setWsdlURL("classpath:Henvendelse.wsdl");
            factoryBean.getFeatures().add(new LoggingFeature());
            factoryBean.getFeatures().add(new WSAddressingFeature());
            factoryBean.getOutInterceptors().add(new SystemSAMLOutInterceptor());
            factoryBean.getOutInterceptors().add(new UserSAMLOutInterceptor());
            factoryBean.setServiceClass(HenvendelsePortType.class);
            factoryBean.setAddress(henvendelseEndpoint);
            HenvendelsePortType henvendelsePortType = factoryBean.create(HenvendelsePortType.class);
            konfigurerMedHttps(henvendelsePortType);

            return henvendelsePortType;
        }

        @Bean
        public Pingable henvendelsePing() {
            return new Pingable() {
                @Override
                public List<PingResult> ping() {
                    long start = System.currentTimeMillis();
                    try {
                        boolean ping = henvendelsePortType().ping();
                        return evaluatePing(start, ping);
                    } catch (Exception e) {
                        return evaluatePing(start, false);
                    }
                }

                List<PingResult> evaluatePing(long startTime, boolean success) {
                    long timeElapsed = System.currentTimeMillis() - startTime;
                    return asList(new PingResult("HenvendelseInnsyn_v1", success ? SERVICE_OK : SERVICE_FAIL, timeElapsed));
                }
            };
        }
    }

    @Profile({"test", "henvendelseinnsynTest"})
    @Configuration
    public static class Test {

        @Bean
        public HenvendelsePortType henvendelsePortType() {
            final Integer traadId = Integer.valueOf(BesvareHenvendelsePortTypeMock.TRAAD);
            return new HenvendelsePortType() {

                private static final String SPORSMAL = "SPORSMAL";
                private static final String SVAR = "SVAR";
                List<WSHenvendelse> henvendelser = asList(
                        createWSHenvendelse(SPORSMAL, "" + traadId, Tema.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT, DateTime.now().minusWeeks(2)),
                        createWSHenvendelse(SPORSMAL, "" + (traadId + 1), Tema.INTERNASJONALT, DateTime.now().minusWeeks(1)),
                        createWSHenvendelse(SVAR, "" + (traadId + 1), Tema.INTERNASJONALT, DateTime.now().minusDays(5), DateTime.now().minusDays(4)),
                        createWSHenvendelse(SPORSMAL, "" + (traadId + 2), Tema.HJELPEMIDLER, DateTime.now().minusDays(3)),
                        createWSHenvendelse(SVAR, "" + (traadId + 2), Tema.HJELPEMIDLER, DateTime.now().minusHours(5), null),
                        createWSHenvendelse(SPORSMAL, "" + (traadId + 3), Tema.HJELPEMIDLER, DateTime.now().minusHours(10)));

                @Override
                public void merkMeldingSomLest(String id) {
                    LOG.info("Henvendelse med id " + id + " er lest.");
                }

                @Override
                public boolean ping() {
                    return true;
                }

                @Override
                public List<WSHenvendelse> hentHenvendelseListe(String fnr, List<String> henvendelseType) {
                    LOG.info("Henter alle henvendelser for bruker med fødselsnummer " + fnr);
                    return henvendelser;
                }


                WSHenvendelse createWSHenvendelse(String type, String traad, Tema tema, DateTime opprettet) {
                    return createWSHenvendelse(type, traad, tema, opprettet, opprettet);
                }

                WSHenvendelse createWSHenvendelse(String type, String traad, Tema tema, DateTime opprettet, DateTime lestdato) {
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
            };
        }
    }
}
