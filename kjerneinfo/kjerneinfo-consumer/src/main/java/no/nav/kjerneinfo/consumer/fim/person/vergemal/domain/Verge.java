package no.nav.kjerneinfo.consumer.fim.person.vergemal.domain;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.domain.person.Personnavn;

public class Verge {

    private String ident;
    private Personnavn personnavn;
    private Kodeverdi vergesakstype;
    private Kodeverdi mandattype;
    private String mandattekst;
    private Periode virkningsperiode;
    private Kodeverdi embete;
    private Kodeverdi vergetype;

    public Verge withIdent(String ident) {
        this.ident = ident;
        return this;
    }

    public String getIdent() {
        return ident;
    }

    public Verge withPersonnavn(Personnavn personnavn) {
        this.personnavn = personnavn;
        return this;
    }

    public Personnavn getPersonnavn() {
        return personnavn;
    }

    public Verge withSakstype(Kodeverdi vergesakstype) {
        this.vergesakstype = vergesakstype;
        return this;
    }

    public Verge withMandattype(Kodeverdi mandattype) {
        this.mandattype = mandattype;
        return this;
    }

    public Verge withMandattekst(String mandattekst) {
        this.mandattekst = mandattekst;
        return this;
    }

    public Verge withVirkningsperiode(Periode periode) {
        this.virkningsperiode = periode;
        return this;
    }

    public Verge withEmbete(Kodeverdi embete) {
        this.embete = embete;
        return this;
    }

    public Verge withVergetype(Kodeverdi vergetype) {
        this.vergetype = vergetype;
        return this;
    }

    public Kodeverdi getVergesakstype() {
        return vergesakstype;
    }

    public Kodeverdi getMandattype() {
        return mandattype;
    }

    public String getMandattekst() {
        return mandattekst;
    }

    public Periode getVirkningsperiode() {
        return virkningsperiode;
    }

    public Kodeverdi getEmbete() {
        return embete;
    }

    public Kodeverdi getVergetype() {
        return vergetype;
    }

}
