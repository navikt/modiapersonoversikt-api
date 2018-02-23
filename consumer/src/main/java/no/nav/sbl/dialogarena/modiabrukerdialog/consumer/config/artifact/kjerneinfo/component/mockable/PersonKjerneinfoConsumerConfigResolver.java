package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandleperson.v1.*;
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.kjerneinfo.consumer.fim.behandleperson.config.BehandlePersonEndpointConfig.TPS_BEHANDLEPERSON_V1_MOCK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class PersonKjerneinfoConsumerConfigResolver {

    @Inject
    @Qualifier("behandlePersonServiceDefault")
    private Wrapper<BehandlePersonServiceBi> behandlePersonServiceDefault;

    @Inject
    @Qualifier("behandlePersonServiceMock")
    private Wrapper<BehandlePersonServiceBi> behandlePersonServiceMock;

    @Bean
    public BehandlePersonServiceBi behandlePersonServiceBi() {
        return new BehandlePersonServiceBi() {
            @Override
            public WSEndreNavnResponse endreNavn(WSEndreNavnRequest endreNavnRequest) throws Sikkerhetsbegrensning, PersonIkkeFunnet, UgyldigInput, PersonIkkeUtvandret {
                if (mockErTillattOgSlaattPaaForKey(TPS_BEHANDLEPERSON_V1_MOCK_KEY)) {
                    return behandlePersonServiceMock.wrappedObject.endreNavn(endreNavnRequest);
                }
                return behandlePersonServiceDefault.wrappedObject.endreNavn(endreNavnRequest);
            }
        };
    }

}
