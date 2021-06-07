package no.nav.modiapersonoversikt.legacy.api.utils.henvendelse.delsvar;

import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;

import java.util.*;

public class DelsvarUtils {

    public static boolean harAvsluttendeSvarEtterDelsvar(List<Melding> traad) {
        List<Melding> sortertMedNyestForst = sorterMedNyesteForst(traad);

        Iterator<Melding> iterator = sortertMedNyestForst.iterator();
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

    private static List<Melding> sorterMedNyesteForst(List<Melding> traad) {
        List<Melding> listCopy = new ArrayList<>(traad);
        listCopy.sort(Comparator.comparing(melding -> melding.ferdigstiltDato));
        Collections.reverse(listCopy);
        return listCopy;
    }

}
