package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;

import java.util.Comparator;

public class OmvendtKronologiskBehandlingComparator implements Comparator<GenerellBehandling> {

    @Override
    public int compare(GenerellBehandling o1, GenerellBehandling o2) {
        return o2.behandlingDato.compareTo(o1.behandlingDato);
    }
}
