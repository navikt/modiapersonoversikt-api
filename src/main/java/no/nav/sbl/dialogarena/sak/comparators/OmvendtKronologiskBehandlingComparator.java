package no.nav.sbl.dialogarena.sak.comparators;

import java.time.LocalDate;
import java.util.Comparator;

public class OmvendtKronologiskBehandlingComparator implements Comparator<LocalDate> {

    @Override
    public int compare(LocalDate o1, LocalDate o2) {
        return o2.compareTo(o1);
    }
}
