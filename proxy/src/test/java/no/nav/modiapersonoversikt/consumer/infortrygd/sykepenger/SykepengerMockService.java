package no.nav.modiapersonoversikt.consumer.infortrygd.sykepenger;

import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.SykepengerServiceBi;
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.SykepengerMapper;
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.to.SykepengerResponse;

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
