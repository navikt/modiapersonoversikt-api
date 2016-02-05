package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;


import no.nav.dkif.consumer.DkifServiceBi;
import no.nav.dkif.consumer.support.DefaultDkifService;
import no.nav.modig.modia.ping.PingResult;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class DkifConsumerConfigResolver {

    @Inject
    @Qualifier("dkifDefaultService")
    private Wrapper<DefaultDkifService> dkifDefaultService;

    @Inject
    @Qualifier("dkifMockService")
    private Wrapper<DkifServiceBi> dkifMockService;

    @Bean
    public DkifServiceBi dkifServiceBi() {
        return new DkifServiceBi() {
            @Override
            public WSHentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(WSHentDigitalKontaktinformasjonRequest request)
                    throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonPersonIkkeFunnet {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return dkifMockService.wrappedObject.hentDigitalKontaktinformasjon(request);
                } else {
                    return dkifDefaultService.wrappedObject.hentDigitalKontaktinformasjon(request);
                }
            }
        };
    }

}
