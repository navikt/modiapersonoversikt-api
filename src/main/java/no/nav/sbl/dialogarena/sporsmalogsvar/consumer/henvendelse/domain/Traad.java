package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Traad {

    private List<Melding> meldinger;

    public Traad(List<Melding> meldinger) {
        if (harAvsluttendeSvarEtterDelsvar(meldinger)) {
            this.meldinger = sammenslaFullforteDelsvar(meldinger);
        } else {
            this.meldinger = meldinger;
        }
    }

    private boolean harAvsluttendeSvarEtterDelsvar(List<Melding> traad) {
        List<Melding> sorterMedNyesteForst = sorterMedNyesteForst(traad);

        Iterator<Melding> iterator = sorterMedNyesteForst.iterator();
        Melding current = iterator.next();
        while (iterator.hasNext()) {
            Melding next = iterator.next();
            if (current.erSvarSkriftlig() && next.erDelvisSvar()) {
                return true;
            }
            current = next;
        }
        return false;
    }

    private List<Melding> sorterMedNyesteForst(List<Melding> traad) {
        List<Melding> listCopy = traad.subList(0, traad.size());
        listCopy.sort(Comparator.comparing(melding -> melding.opprettetDato));
        Collections.reverse(listCopy);
        return listCopy;
    }

    private List<Melding> sammenslaFullforteDelsvar(List<Melding> meldinger) {
        MeldingSammenslaaer meldingSammenslaaer = new MeldingSammenslaaer(meldinger);
        return meldingSammenslaaer.sammenslaFullforteDelsvar();
    }

    public String getTraadId() {
        return getRotmelding().traadId;
    }

    public List<Melding> getMeldinger() {
        return meldinger;
    }

    private Melding getRotmelding() {
        return meldinger.stream()
                .filter(melding -> melding.id.equals(melding.traadId))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

}
