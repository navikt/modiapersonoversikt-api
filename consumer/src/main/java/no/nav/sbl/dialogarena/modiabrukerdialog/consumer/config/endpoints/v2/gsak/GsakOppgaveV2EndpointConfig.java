package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeFilter;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeSok;
import no.nav.virksomhet.tjenester.oppgave.v2.binding.Oppgave;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakTjenesteSikkerhet.STANDARD_BRUKERNAVN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakTjenesteSikkerhet.STANDARD_PASSORD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakTjenesteSikkerhet.leggPaaAutentisering;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV2PortTypeMock.createOppgavePortTypeMock;

@Configuration
public class GsakOppgaveV2EndpointConfig {

    public static final String GSAK_V2_KEY = "start.gsak.oppgave.withmock";

    @Bean
    public Oppgave gsakOppgavePortType() {
        return createSwitcher(createOppgavePortType(), createOppgavePortTypeMock(), GSAK_V2_KEY, Oppgave.class);
    }

    @Bean
    public Pingable gsakPing() {
        final Oppgave ws = createOppgavePortType();
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "GSAK_V2";
                try {
                    FinnOppgaveListeRequest finnOppgaveListeRequest = new FinnOppgaveListeRequest();
                    FinnOppgaveListeSok finnOppgaveListeSok = new FinnOppgaveListeSok();
                    finnOppgaveListeSok.setBrukerId("10108000398");
                    FinnOppgaveListeFilter finnOppgaveListeFilter = new FinnOppgaveListeFilter();
                    finnOppgaveListeFilter.setMaxAntallSvar(0);
                    finnOppgaveListeRequest.setSok(finnOppgaveListeSok);
                    finnOppgaveListeRequest.setFilter(finnOppgaveListeFilter);
                    ws.finnOppgaveListe(finnOppgaveListeRequest);
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static Oppgave createOppgavePortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:wsdl/no/nav/virksomhet/tjenester/oppgave/oppgave.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("gsak.oppgave.v2.url"));
        proxyFactoryBean.setServiceClass(Oppgave.class);
        proxyFactoryBean.getFeatures().add(new LoggingFeature());

        leggPaaAutentisering(proxyFactoryBean, STANDARD_BRUKERNAVN, STANDARD_PASSORD);

        return proxyFactoryBean.create(Oppgave.class);
    }

}
