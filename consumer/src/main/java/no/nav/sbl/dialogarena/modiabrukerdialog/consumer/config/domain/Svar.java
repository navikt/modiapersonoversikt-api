package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

public class Svar {

    public String sporsmalsId, fnr, navIdent, temagruppe, kanal, fritekst;

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

    public Svar withTemagruppe(String temagruppe){
        this.temagruppe = temagruppe;
        return this;
    }

    public Svar withKanal(String kanal){
        this.kanal = kanal;
        return this;
    }

    public Svar withFritekst(String fritekst){
        this.fritekst = fritekst;
        return this;
    }
}
