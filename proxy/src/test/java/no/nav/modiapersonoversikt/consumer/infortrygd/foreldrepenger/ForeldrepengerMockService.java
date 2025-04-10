package no.nav.modiapersonoversikt.consumer.infortrygd.foreldrepenger;

import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeResponse;

public class ForeldrepengerMockService implements ForeldrepengerServiceBi {

    private final ForeldrepengerMapper mapper;

    public ForeldrepengerMockService(ForeldrepengerMapper foreldrepengerMapper) {
        this.mapper = foreldrepengerMapper;
    }

    @Override
    public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {
        return mapper.map(ForeldrepengerMockFactory.createFimHentForeldrepengerListeResponse());
    }
}
