package no.nav.modiapersonoversikt.config.artifact.kjerneinfo;

import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;

public class KontrakterConsumerConfigImpl {

    public static DefaultYtelseskontraktService createYtelseskontraktService(YtelseskontraktV3 ytelseskontraktV3) {
        DefaultYtelseskontraktService ytelseskontraktService = new DefaultYtelseskontraktService();
        ytelseskontraktService.setYtelseskontraktService(ytelseskontraktV3);
        ytelseskontraktService.setMapper(YtelseskontraktMapper.getInstance());
        return ytelseskontraktService;
    }

    public static DefaultOppfolgingskontraktService createOppfolgingskontraktService(OppfoelgingPortType oppfoelgingPortType) {
        DefaultOppfolgingskontraktService oppfolgingskontraktServiceBi = new DefaultOppfolgingskontraktService();
        oppfolgingskontraktServiceBi.setOppfolgingskontraktService(oppfoelgingPortType);
        oppfolgingskontraktServiceBi.setMapper(OppfolgingskontraktMapper.getInstance());
        return oppfolgingskontraktServiceBi;
    }

}
