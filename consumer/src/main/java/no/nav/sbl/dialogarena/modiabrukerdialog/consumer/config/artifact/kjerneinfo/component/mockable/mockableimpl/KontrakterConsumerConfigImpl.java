package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;

public class KontrakterConsumerConfigImpl {

    public static DefaultYtelseskontraktService createYtelseskontraktService(YtelseskontraktV3 ytelseskontraktV3, YtelseskontraktV3 selftestYtelseskontraktV3) {
        DefaultYtelseskontraktService ytelseskontraktService = new DefaultYtelseskontraktService();
        ytelseskontraktService.setYtelseskontraktService(ytelseskontraktV3);
        ytelseskontraktService.setSelftestYtelseskontraktService(selftestYtelseskontraktV3);
        ytelseskontraktService.setMapper(new YtelseskontraktMapper());
        return ytelseskontraktService;
    }

    public static DefaultOppfolgingskontraktService createOppfolgingskontraktService(OppfoelgingPortType oppfoelgingPortType, OppfoelgingPortType selftestOppfoelgingPortType) {
        DefaultOppfolgingskontraktService oppfolgingskontraktServiceBi = new DefaultOppfolgingskontraktService();
        oppfolgingskontraktServiceBi.setOppfolgingskontraktService(oppfoelgingPortType);
        oppfolgingskontraktServiceBi.setSelftestOppfolgingskontraktService(selftestOppfoelgingPortType);
        oppfolgingskontraktServiceBi.setMapper(new OppfolgingskontraktMapper());
        return oppfolgingskontraktServiceBi;
    }

}
