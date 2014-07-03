package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;

import java.util.Comparator;

public class SistOppdaterteBehandlingComparator implements Comparator<TemaVM> {

    @Override
    public int compare(TemaVM o1, TemaVM o2) {
        return new OmvendtKronologiskBehandlingComparator().compare(o1.sistoppdaterteBehandling, o2.sistoppdaterteBehandling);
    }

}
