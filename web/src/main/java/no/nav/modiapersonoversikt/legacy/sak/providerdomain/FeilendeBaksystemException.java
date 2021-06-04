package no.nav.modiapersonoversikt.legacy.sak.providerdomain;

public class FeilendeBaksystemException extends RuntimeException {

    private Baksystem baksystem;

    public FeilendeBaksystemException(Baksystem baksystem){
        super();
        this.baksystem = baksystem;
    }

    public Baksystem getBaksystem() {
        return baksystem;
    }
}
