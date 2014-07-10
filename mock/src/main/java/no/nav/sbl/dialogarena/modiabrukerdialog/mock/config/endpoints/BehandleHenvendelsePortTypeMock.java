package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BehandleHenvendelsePortTypeMock {

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        return createBehandleHenvendelsePortTypeMock();
    }

    public static BehandleHenvendelsePortType createBehandleHenvendelsePortTypeMock() {
        return new BehandleHenvendelsePortType() {

            @Override
            public void oppdaterOpprettetOppgaveIdGsak(String behandlingsId, String oppgaveIdGsak) {
            }

            @Override
            public void oppdaterJournalfortInformasjon(String behandlingsId, XMLJournalfortInformasjon journalfortInformasjon) {
            }

            @Override
            public void ping() {
            }
        };
    }
}
