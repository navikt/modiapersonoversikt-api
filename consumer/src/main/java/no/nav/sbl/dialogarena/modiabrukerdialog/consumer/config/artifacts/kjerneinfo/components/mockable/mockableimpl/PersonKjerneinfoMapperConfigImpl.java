package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl;

import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;

public class PersonKjerneinfoMapperConfigImpl {

    private KodeverkmanagerBi kodeverkmanagerBean;

    public PersonKjerneinfoMapperConfigImpl(KodeverkmanagerBi kodeverkmanagerBean) {
        this.kodeverkmanagerBean = kodeverkmanagerBean;
    }

    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(kodeverkmanagerBean);
    }

}
