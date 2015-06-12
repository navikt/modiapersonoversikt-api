package no.nav.sbl.dialogarena.sak.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.journal.v1.Journal_v1PortType;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;

@Configuration
public class IntegrationTestOverridesConfig {

    @Bean
    @Named("henvendelseSoknaderPortType")
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        return mock(HenvendelseSoknaderPortType.class);
    }

    @Bean
    @Named("sakEndpoint")
    public SakV1 sakEndpoint() {
        return mock(SakV1.class);
    }

    @Bean
    @Named("joarkPortType")
    public Journal_v1PortType joarkPortType() {
        return mock(Journal_v1PortType.class);
    }

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        return mock(SakOgBehandling_v1PortType.class);
    }

    @Bean(name = "pep")
    public EnforcementPoint pep() {
        return mock(EnforcementPoint.class);
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        CmsContentRetriever cmsMock = new CmsContentRetriever() {
            @Override
            public String hentTekst(String key) {
                switch (key) {
                    case "mange.saker":
                        return "Vis alle {0} saker";
                    case "ingen.saker":
                        return "finnes ikke noen saker";
                    case "saker.feilet":
                        return "kan ikke vise saker'";
                    case "filter.ulovligesakstema":
                        return "FEI,SAK,SAP";
                    case "filter.lovligebehandlingstyper":
                        return "ae0047,ae0034,ae0014";
                    default:
                        return "default tekst fra CMS-mock";
                }
            }

            @Override
            public String hentArtikkel(String key) {
                switch (key) {
                    default:
                        return "default tekst fra CMS-mock";
                }
            }
        };
        return cmsMock;
    }

    @Bean
    public AktoerPortType fodselnummerAktorService() {
        return mock(AktoerPortType.class, RETURNS_MOCKS);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        return mock(KodeverkClient.class);
    }
}
