package no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Pleiepengerrettighet;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class PleiepengerListeResponse implements Serializable {

    private final List<Pleiepengerrettighet> pleieepengerettighetListe;

    public PleiepengerListeResponse(List<Pleiepengerrettighet> pleiepengerettighet) {
        this.pleieepengerettighetListe = pleiepengerettighet;
    }

    public  List<Pleiepengerrettighet> getPleieepengerettighetListe() {
        return pleieepengerettighetListe;
    }

    public Optional<Pleiepengerrettighet> getPleiepengerRettighet(String barnetsFnr) {
        return pleieepengerettighetListe.stream()
                .filter(pleiepengerrettighet -> pleiepengerrettighet.getBarnet().equals(barnetsFnr))
                .findFirst();
    }

}
