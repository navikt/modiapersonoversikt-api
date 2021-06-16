package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.SykepengerMapper;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;

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
