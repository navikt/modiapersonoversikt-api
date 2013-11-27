package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;

import java.util.Comparator;


public class MottakerComparator implements Comparator<Mottaker> {

    @Override
    public int compare(Mottaker o1, Mottaker o2) {
        return o1.getNavn().compareTo(o2.getNavn());
    }

    // CHECKSTYLE:OFF
    public static boolean equals(Mottaker left, Mottaker right) {
        if (left == right) { return true;}
        if((left == null) || (right == null)) { return false; }

        if (left.getMottakertypeType() != null ? !left.getMottakertypeType().equals(right.getMottakertypeType()) : right.getMottakertypeType() != null)
        { return false; }
        return !(left.getNavn() != null ? !left.getNavn().equals(right.getNavn()) : right.getNavn() != null);
    }
    // CHECKSTYLE:ON
}
