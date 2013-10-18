package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.Utils.konfigurerMedHttps;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.Tema.FAMILIE_OG_BARN;

@Configuration
public class BesvareHenvendelseTjenesteConfig {

    @Configuration
    public static class Default {
        @Value("${besvarehenvendelseendpoint.url}")
        protected String besvareHenvendelseEndpoint;

        @Bean
        public BesvareHenvendelsePortType besvareHenvendelsePortType() {
            return lagBesvareHenvendelsePortType(new UserSAMLOutInterceptor());
        }

        @Bean
        public Pingable besvareHenvendelsePing() {
            return new Pingable() {
                @Override
                public List<PingResult> ping() {
                    BesvareHenvendelsePortType besvareHenvendelsePortType = lagBesvareHenvendelsePortType(new SystemSAMLOutInterceptor());
                    long start = System.currentTimeMillis();
                    boolean success;
                    try {
                        success = besvareHenvendelsePortType.ping();
                    } catch (Exception e) {
                        success = false;
                    }
                    return asList(new PingResult("BesvareHenvendelse_v1", success ? SERVICE_OK : SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            };
        }

        private BesvareHenvendelsePortType lagBesvareHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
            JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
            factoryBean.setWsdlURL("classpath:v1/BesvareHenvendelse.wsdl");
            factoryBean.getFeatures().add(new LoggingFeature());
            factoryBean.getFeatures().add(new WSAddressingFeature());
            factoryBean.getOutInterceptors().add(interceptor);
            factoryBean.setServiceClass(BesvareHenvendelsePortType.class);
            factoryBean.setAddress(besvareHenvendelseEndpoint);
            BesvareHenvendelsePortType besvareHenvendelsePortType = factoryBean.create(BesvareHenvendelsePortType.class);
            konfigurerMedHttps(besvareHenvendelsePortType);

            return besvareHenvendelsePortType;
        }
    }

    @Configuration
    public static class Test {

        public static final Integer TRAAD_ID = 1;

        @Bean
        public BesvareHenvendelsePortType besvareHenvendelsePortType() {
            return new BesvareHenvendelsePortType() {
                @Override
                public boolean ping() {
                    return true;
                }

                @Override
                public void besvarSporsmal(WSSvar wsSvar) {
                }

                @Override
                public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
                    Random random = new Random();
                    return new WSSporsmalOgSvar()
                            .withSporsmal(new WSSporsmal()
                                    .withBehandlingsId("" + random.nextInt())
                                    .withTraad("" + TRAAD_ID)
                                    .withOpprettet(DateTime.now())
                                    .withTema(FAMILIE_OG_BARN.toString())
                                    .withFritekst("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore " +
                                            "magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut " +
                                            "aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel " +
                                            "illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril " +
                                            "delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet" +
                                            " doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit " +
                                            "eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus"))
                            .withSvar(new WSSvar().withBehandlingsId("" + random.nextInt()));
                }
            };
        }

    }
}
