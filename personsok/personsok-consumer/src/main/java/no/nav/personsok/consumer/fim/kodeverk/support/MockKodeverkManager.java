package no.nav.personsok.consumer.fim.kodeverk.support;

import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class MockKodeverkManager implements KodeverkManager {

    @Override
    public String getBeskrivelseForKode(String kodeValue, String kodeverksref, String spraak) {
        return EMPTY;
    }
}
