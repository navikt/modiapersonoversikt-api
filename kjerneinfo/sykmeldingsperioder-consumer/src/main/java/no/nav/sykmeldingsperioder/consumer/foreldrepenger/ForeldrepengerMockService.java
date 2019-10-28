package no.nav.sykmeldingsperioder.consumer.foreldrepenger;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;

public class ForeldrepengerMockService implements ForeldrepengerServiceBi {

    private final ForeldrepengerMapper mapper;

    public ForeldrepengerMockService(ForeldrepengerMapper foreldrepengerMapper) {
        this.mapper = foreldrepengerMapper;
    }

    @Override
    public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {
        return mapper.map(ForeldrepengerMockFactory.createFimHentForeldrepengerListeResponse(), ForeldrepengerListeResponse.class);
    }

}
