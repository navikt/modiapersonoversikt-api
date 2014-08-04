package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLOppgaveOpprettetInformasjon;
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
            public void oppdaterJournalfortInformasjon(String behandlingId, XMLJournalfortInformasjon journalfortInformasjon) {
            }

            @Override
            public void oppdaterOppgaveOpprettetInformasjon(XMLOppgaveOpprettetInformasjon oppdaterOppgaveOpprettetInformasjon) {
            }

            @Override
            public void oppdaterHenvendelsesarkivInformasjon(String behandlingId, String arkivpostId) {
            }

            @Override
            public void ping() {
            }
        };
    }
}
