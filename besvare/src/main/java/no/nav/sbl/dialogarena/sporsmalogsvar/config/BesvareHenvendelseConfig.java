package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.mock.BesvareHenvendelsePortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Temaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({BesvareHenvendelseConfig.Default.class, BesvareHenvendelseConfig.DefaultWithoutCNCheck.class, BesvareHenvendelseTjenester.class, BesvareHenvendelseConfig.Test.class})
public class BesvareHenvendelseConfig {

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return new SakOgBehandlingPortType() {

            @Override
            public void ping() {
            }

            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(HentBehandlingskjedensBehandlingerRequest request) {
                return null;
            }

            @Override
            public HentBehandlingResponse hentBehandling(HentBehandlingRequest request) {
                return null;
            }

            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(FinnSakOgBehandlingskjedeListeRequest request) {
                Behandlingskjede kjede = new Behandlingskjede()
                        .withNormertBehandlingstid(new Behandlingstid().withType(new Behandlingstidtyper()))
                        .withBehandlingskjedetype(new Behandlingskjedetyper())
                        .withBehandlingskjedeId("id")
//		            .withStartNAVtid(createXmlGregorianDate(1, 1, 2013))
//		            .withKjedensNAVfrist(createXmlGregorianDate(11, 11, 2022))
                        .withSisteBehandlingREF("sisteBehandlingref")
                        .withSisteBehandlingsstegREF("sisteBehandlingsstegref")
                        .withSisteBehandlingsstegtype(new Behandlingsstegtyper().withValue("value"));
                return new FinnSakOgBehandlingskjedeListeResponse().
                        withSak(new Sak().withTema(new Temaer().withKodeverksRef("Alderspensjon")).withBehandlingskjede(kjede),
                                new Sak().withTema(new Temaer().withKodeverksRef("Foreldrepenger")).withBehandlingskjede(kjede));
            }

        };
    }

    @Profile({"default", "brukerhenvendelserDefault"})
    @Configuration
    @Import({JaxWsFeatures.Integration.class})
    public static class Default { }

    @Profile({"brukerhenvendelserDefaultWithoutCNCheck"})
    @Configuration
    @Import({JaxWsFeatures.Mock.class})
    public static class DefaultWithoutCNCheck { }

    @Profile({"test", "brukerhenvendelserTest"})
    @Configuration
    public static class Test {
        @Bean
        public BesvareHenvendelsePortType besvareSso() {
            return new BesvareHenvendelsePortTypeMock();
        }
    }
}