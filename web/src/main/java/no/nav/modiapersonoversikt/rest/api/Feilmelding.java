package no.nav.modiapersonoversikt.rest.api;

public class Feilmelding {
    private String message;

    public Feilmelding withMessage(String message) {
        this.message = message;
        return this;
    }

    public String getMessage() {
        return message;
    }
}
