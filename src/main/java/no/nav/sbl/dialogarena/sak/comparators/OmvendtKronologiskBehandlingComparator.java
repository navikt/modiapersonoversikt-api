package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;

import java.util.Comparator;

public class OmvendtKronologiskBehandlingComparator implements Comparator<GenerellBehandling> {

    @Override
    public int compare(GenerellBehandling o1, GenerellBehandling o2) {
        if (o2 == null && o1 == null) {
            return 0;
        } else if (o2 == null) {
            return -1;
        } else if (o1 == null) {
            return 1;
        }
        return o2.behandlingDato.compareTo(o1.behandlingDato);
    }
}
