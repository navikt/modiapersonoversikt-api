package no.nav.modig.modia.ping;

/**
 * Defines a method to check whether a component is available
 */
public interface Pingable {

    /**
     * Checks if a component is available with all available dependencies.
     * Should throw SystemException if the component is unavailable
     * @return long - the time it takes for the component to reply
     */
    PingResult ping();

    /**
     * The name of the corresponding webservice. Used by selftest-display.
     */
    String name();

    /**
     * What kind of method that is used to check the underlying webservice
     */
    String method();

    /**
     * The endpoint of the webservice
     */
    String endpoint();



}
