package no.nav.modiapersonoversiktproxy.consumer.infortrygd.sykepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.SykepengerServiceBi;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.SykepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerResponse;

public class SykepengerMockService implements SykepengerServiceBi {

    private final SykepengerMapper mapper;

    public SykepengerMockService(SykepengerMapper sykepengerMapper) {
        this.mapper = sykepengerMapper;
    }

    @Override
    public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
        return mapper.map(SykepengerMockFactory.createFimHentSykepengerResponse());
    }

}
