package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.KodeverkmanagerBiMock.getKodeverkmanagerBiMock;

@Configuration
@Import({
        no.nav.kodeverk.consumer.config.ConsumerConfig.class
})
public class PersonKjerneinfoMapperConfigResolver {

    @Inject
    private KodeverkmanagerBi kodeverkmanagerBean;
    private KodeverkmanagerBi kodeverkmanagerBeanMock = getKodeverkmanagerBiMock();

    @Bean
    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(kodeverksManagerBean());
    }

    private KodeverkmanagerBi kodeverksManagerBean() {
        return new KodeverkmanagerBi() {
            @Override
            public String getBeskrivelseForKode(String koderef, String kodeverksref, String spraak) throws HentKodeverkKodeverkIkkeFunnet {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return kodeverkmanagerBeanMock.getBeskrivelseForKode(koderef, kodeverksref, spraak);
                }
                return kodeverkmanagerBean.getBeskrivelseForKode(koderef, kodeverksref, spraak);
            }

            @Override
            public List<Kodeverdi> getKodeverkList(String kodeverksref, String spraak) throws HentKodeverkKodeverkIkkeFunnet {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return kodeverkmanagerBeanMock.getKodeverkList(kodeverksref, spraak);
                }
                return kodeverkmanagerBean.getKodeverkList(kodeverksref, spraak);
            }

            @Override
            public String getTelefonLand(String landkode) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return kodeverkmanagerBeanMock.getTelefonLand(landkode);
                }
                return kodeverkmanagerBean.getTelefonLand(landkode);
            }

        };
    }

}
