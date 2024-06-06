package no.nav.modiapersonoversiktproxy.consumer.infortrygd.foreldrepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeResponse;

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
