package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;

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
