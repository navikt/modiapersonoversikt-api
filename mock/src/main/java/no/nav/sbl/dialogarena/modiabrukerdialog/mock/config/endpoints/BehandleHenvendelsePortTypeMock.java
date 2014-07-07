package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.meldinger.WSOppdaterInformasjonRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.meldinger.WSOppdaterInformasjonResponse;
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
            public WSOppdaterInformasjonResponse oppdaterArkivInformasjon(WSOppdaterInformasjonRequest parameters) {
                return new WSOppdaterInformasjonResponse();
            }

            @Override
            public void oppdaterOpprettetOppgaveIdGsak(String behandlingsId, String oppgaveIdGsak) {
            }

            @Override
            public WSOppdaterInformasjonResponse oppdaterJournalforingInformasjon(WSOppdaterInformasjonRequest parameters) {
                return new WSOppdaterInformasjonResponse();
            }

            @Override
            public void ping() {
            }
        };
    }
}
