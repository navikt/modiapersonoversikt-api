package no.nav.kjerneinfo.consumer.fim.person.vergemal.domain;

import javax.xml.datatype.XMLGregorianCalendar;

public class Periode {

    private final XMLGregorianCalendar fom;
    private final XMLGregorianCalendar tom;

    public Periode(XMLGregorianCalendar fom, XMLGregorianCalendar tom) {
        this.fom = fom;
        this.tom = tom;
    }

    public XMLGregorianCalendar getFom() {
        return fom;
    }

    public XMLGregorianCalendar getTom() {
        return tom;
    }

}
