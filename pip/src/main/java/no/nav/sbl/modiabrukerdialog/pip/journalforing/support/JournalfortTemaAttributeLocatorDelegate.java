package no.nav.sbl.modiabrukerdialog.pip.journalforing.support;

import java.util.Set;

public interface JournalfortTemaAttributeLocatorDelegate {

    /**
     * Henter temagrupper som ansattes valgte enhet har tilgang til
     *
     * @return Set med temagrupper
     */
    Set<String> getTemagrupperForAnsattesValgteEnhet(String ansattId);
}
