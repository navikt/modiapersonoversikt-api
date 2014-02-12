package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockSetupErTillatt;

@Configuration
public class KjerneinfoMapperConfigResolver {

    @Inject
    @Qualifier("kodeverkManagerService")
    private Wrapper<KodeverkmanagerBi> kodeverkManagerService;

    @Inject
    @Qualifier("kodeverkManagerMock")
    private Wrapper<KodeverkmanagerBi> kodeverkManagerMock;

    @Bean
    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(
                new KodeverkmanagerBi() {
                    @Override
                    public String getBeskrivelseForKode(String koderef, String kodeverksref, String spraak) throws HentKodeverkKodeverkIkkeFunnet {
                        if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                            return kodeverkManagerMock.wrappedObject.getBeskrivelseForKode(koderef, kodeverksref, spraak);
                        }
                        return kodeverkManagerService.wrappedObject.getBeskrivelseForKode(koderef, kodeverksref, spraak);
                    }

                    @Override
                    public List<Kodeverdi> getKodeverkList(String kodeverksref, String spraak) throws HentKodeverkKodeverkIkkeFunnet {
                        if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                            return kodeverkManagerMock.wrappedObject.getKodeverkList(kodeverksref, spraak);
                        }
                        return kodeverkManagerService.wrappedObject.getKodeverkList(kodeverksref, spraak);
                    }

                    @Override
                    public String getTelefonLand(String landkode) {
                        if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                            return kodeverkManagerMock.wrappedObject.getTelefonLand(landkode);
                        }
                        return kodeverkManagerService.wrappedObject.getTelefonLand(landkode);
                    }

                }
        );

    }

}
