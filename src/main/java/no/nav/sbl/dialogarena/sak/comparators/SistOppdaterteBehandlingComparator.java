package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;

import java.util.Comparator;

public class SistOppdaterteBehandlingComparator implements Comparator<Tema> {

    @Override
    public int compare(Tema o1, Tema o2) {
        return o2.behandlingsdato.compareTo(o1.behandlingsdato);
    }

}
