package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.transform.Mergeable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;


public class MergeUtil {

    public static <U> List<U> merge(List<Mergeable> inputListe, Comparator<Mergeable> comparator, Comparator<Mergeable> sorter){
        LinkedList<Mergeable> ytelser = on(inputListe).collectIn(new LinkedList<Mergeable>());
        sort(ytelser, sorter);

        List<U> resultat = new ArrayList<>();
        while (ytelser.size() >= 1) {
            Mergeable first = ytelser.get(0);

            List<Mergeable> skalMerges = new ArrayList<>();
            skalMerges.add(first);

            for (Mergeable other : ytelser.subList(1, ytelser.size())) {
                if (comparator.compare(first,other) == 0) {
                    skalMerges.add(other);
                }
            }
            resultat.add((U) first.doMerge(skalMerges));
            ytelser.removeAll(skalMerges);
        }

        return resultat;
    }
}
