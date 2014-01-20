package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.transform.Mergeable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;


public class MergeUtil {

    public static <U> List<U> merge(List<Mergeable<U>> inputListe, Comparator<Mergeable<U>> comparator, Comparator<Mergeable<U>>... sorters){
        List<Mergeable<U>> ytelser = new ArrayList<>(inputListe);

        for(Comparator<Mergeable<U>> sorter : sorters) {
            ytelser = on(inputListe).collect(sorter);
        }

        List<U> resultat = new ArrayList<>();
        while (ytelser.size() >= 1) {
            Mergeable<U> first = ytelser.get(0);

            List<Mergeable> skalMerges = new ArrayList<>();
            skalMerges.add(first);

            for (Mergeable<U> other : ytelser.subList(1, ytelser.size())) {
                if (comparator.compare(first, other) == 0) {
                    skalMerges.add(other);
                }
            }
            resultat.add(first.doMerge(skalMerges));
            ytelser.removeAll(skalMerges);
        }

        return resultat;
    }
}
