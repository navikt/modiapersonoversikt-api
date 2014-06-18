package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

public class Referat {

    public String fnr, navIdent, tema, kanal, fritekst;

    public Referat withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public Referat withNavIdent(String navIdent) {
        this.navIdent = navIdent;
        return this;
    }

    public Referat withTema(String tema){
        this.tema = tema;
        return this;
    }

    public Referat withKanal(String kanal){
        this.kanal = kanal;
        return this;
    }

    public Referat withFritekst(String fritekst){
        this.fritekst = fritekst;
        return this;
    }

}
