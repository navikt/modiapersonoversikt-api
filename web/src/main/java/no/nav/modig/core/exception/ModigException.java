package no.nav.modig.core.exception;

/**
 * Toppnivåklasse for unntak in Modig. id benyttes for internasjonalisering av feilmelding i wicket-applikasjoner og kan også
 * benyttes for generell identifisering av exception ellers
 */

public abstract class ModigException extends RuntimeException {
    private String id;

    public ModigException(String melding) {
        super(melding);
    }

    public ModigException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public ModigException(String message, Throwable cause, String id) {
        super(message, cause);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}