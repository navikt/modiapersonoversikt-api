package no.nav.modiapersonoversikt.commondomain.sak;

public class FeilendeBaksystemException extends RuntimeException {

    private final Baksystem baksystem;

    public FeilendeBaksystemException(Baksystem baksystem){
        super();
        this.baksystem = baksystem;
    }

    public Baksystem getBaksystem() {
        return baksystem;
    }
}
