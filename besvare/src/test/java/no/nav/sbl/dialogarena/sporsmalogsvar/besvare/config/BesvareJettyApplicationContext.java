package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.config;

import no.nav.sbl.dialogarena.mottaksbehandling.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.henvendelse.Henvendelser;
import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKoStub;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepo;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepoStub;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgavesystem;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.OppgavesystemStub;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.SakSystemGsak;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.SakSystemPensjon;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.web.BesvareSporsmalApplication;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import no.nav.virksomhet.tjenester.sak.pensjon.meldinger.v1.WSFinnSakListeRequest;
import no.nav.virksomhet.tjenester.sak.pensjon.meldinger.v1.WSFinnSakListeResponse;
import no.nav.virksomhet.tjenester.sak.pensjon.v1.PensjonSak;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import(TjenesterMock.class)
public class BesvareJettyApplicationContext {

    @Bean
    public BesvareSporsmalApplication application() {
        return new BesvareSporsmalApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    static class SakSystemGsakStub implements Sak {

        @Override
        public WSFinnGenerellSakListeResponse finnGenerellSakListe(WSFinnGenerellSakListeRequest wsFinnGenerellSakListeRequest) {
            return new WSFinnGenerellSakListeResponse();
        }
    }

    static class SakSystemPensjonStub implements PensjonSak {

        @Override
        public WSFinnSakListeResponse finnSakListe(WSFinnSakListeRequest request) {
            return new WSFinnSakListeResponse();
        }
    }

    @Bean
    public HenvendelseRepo repo() {
        return new HenvendelseRepoStub();
    }

    @Bean
    public Oppgavesystem oppgavesystem() {
        return new OppgavesystemStub();
    }

    @Bean
    public Mottaksbehandling mottaksbehandling(HenvendelsePortType henvendelsePortType) {
        return new Mottaksbehandling.Default(new MottaksbehandlingKontekst(
                new HendelseKoStub(),
                oppgavesystem(),
                repo(),
                new SakSystemGsak(new SakSystemGsakStub()),
                new SakSystemPensjon(new SakSystemPensjonStub()),
                new Henvendelser(henvendelsePortType)));
    }
}
