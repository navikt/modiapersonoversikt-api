package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v2.YtelseskontraktV2;

public class KontrakterConsumerConfigImpl {

    public static DefaultYtelseskontraktService createYtelseskontraktService(YtelseskontraktV2 ytelseskontraktV2, YtelseskontraktV2 selftestYtelseskontraktV2) {
        DefaultYtelseskontraktService ytelseskontraktService = new DefaultYtelseskontraktService();
        ytelseskontraktService.setYtelseskontraktService(ytelseskontraktV2);
        ytelseskontraktService.setSelftestYtelseskontraktService(selftestYtelseskontraktV2);
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
