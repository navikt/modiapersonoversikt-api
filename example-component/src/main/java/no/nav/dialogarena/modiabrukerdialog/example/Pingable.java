package no.nav.dialogarena.modiabrukerdialog.example;

import java.util.List;

/**
 * Defines a method to check whether a component is available
 */
public interface Pingable {

    /**
     * Checks if a component is available with all available dependencies.
     * Should throw SystemException if the component is unavailable
     * @return long - the time it takes for the component to reply
     */
    List<PingResult> ping();

}
