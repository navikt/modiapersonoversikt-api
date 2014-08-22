package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLOppgaveOpprettetInformasjon;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock.HENVENDELSER;

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
                hentHenvendelse(behandlingId).setJournalfortInformasjon(journalfortInformasjon);
            }

            @Override
            public void oppdaterOppgaveOpprettetInformasjon(XMLOppgaveOpprettetInformasjon oppdaterOppgaveOpprettetInformasjon) {
            }

            @Override
            public void oppdaterHenvendelsesarkivInformasjon(String behandlingId, String arkivpostId) {
            }

            @Override
            public void oppdaterKontorsperre(String enhet, List<String> behandlingsIdListe) {
            }

            @Override
            public void ping() {
            }
        };

    }

    private static XMLHenvendelse hentHenvendelse(String behandlingsId) {
        for (XMLHenvendelse henvendelse : HENVENDELSER) {
            if (behandlingsId.equals(henvendelse.getBehandlingsId())) {
                return henvendelse;
            }
        }
        return new XMLHenvendelse();
    }
}
