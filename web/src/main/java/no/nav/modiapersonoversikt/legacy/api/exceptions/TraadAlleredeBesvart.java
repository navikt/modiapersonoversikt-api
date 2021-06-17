package no.nav.modiapersonoversikt.legacy.api.exceptions;

public class TraadAlleredeBesvart extends RuntimeException {

    public final String traadId;

    public TraadAlleredeBesvart(String traadId) {
        super();
        this.traadId = traadId;
    }

}
