package no.nav.dialogarena.modiabrukerdialog.example;

import no.nav.modig.core.exception.SystemException;

/**
 * Defines a method to check whether a component is available
 */
public interface Pingable {

    /**
     * Checks if a component is available with all available dependencies
     * @return long - the time it takes for the component to reply
     * @throws SystemException - if the component is unavailable
     */
    long ping() throws SystemException;

}
