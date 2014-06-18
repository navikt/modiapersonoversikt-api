package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

public class Svar {

    public String sporsmalsId, fnr, navIdent, tema, fritekst;

    public Svar withSporsmalsId(String sporsmalsId) {
        this.sporsmalsId = sporsmalsId;
        return this;
    }

    public Svar withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public Svar withNavIdent(String navIdent) {
        this.navIdent = navIdent;
        return this;
    }

    public Svar withTema(String tema){
        this.tema = tema;
        return this;
    }

    public Svar withFritekst(String fritekst){
        this.fritekst = fritekst;
        return this;
    }
}
