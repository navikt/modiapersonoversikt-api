package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain;


import java.util.List;

public class Kontaktinformasjon {

    private List<Publikumsmottak> publikumsmottak;

    public Kontaktinformasjon withPublikumsmottakliste(List<Publikumsmottak> publikumsmottakListe) {
        this.publikumsmottak = publikumsmottakListe;
        return this;
    }

    public List<Publikumsmottak> getPublikumsmottak() {
        return publikumsmottak;
    }
}
