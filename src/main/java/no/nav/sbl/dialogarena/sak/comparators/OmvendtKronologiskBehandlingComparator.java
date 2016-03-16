package no.nav.sbl.dialogarena.sak.comparators;


import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;

import java.util.Comparator;

public class OmvendtKronologiskBehandlingComparator implements Comparator<Behandling> {

    @Override
    public int compare(Behandling o1, Behandling o2) {
        return o2.behandlingDato.compareTo(o1.behandlingDato);
    }
}