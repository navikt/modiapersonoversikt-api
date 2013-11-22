package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;

import java.util.Comparator;


public class MottakerComparator implements Comparator<Mottaker> {

    @Override
    public int compare(Mottaker o1, Mottaker o2) {
        return o1.getNavn().compareTo(o2.getNavn());
    }

    public static boolean equals(Mottaker left, Mottaker right) {
        if (left == right) { return true;}
        if(left == null && right == null) { return true; }
        if( (left == null && right != null) || (right == null && left != null)) { return false; }

        if (left.getMottakerId() != null ? !left.getMottakerId().equals(right.getMottakerId()) : right.getMottakerId() != null) return false;
        if (left.getMottakertypeKode() != null ? !left.getMottakertypeKode().equals(right.getMottakertypeKode()) : right.getMottakertypeKode() != null)
            return false;
        if (left.getNavn() != null ? !left.getNavn().equals(right.getNavn()) : right.getNavn() != null) return false;

        return true;
    }
}
