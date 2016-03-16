package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;

import java.util.Comparator;

public class OmvendtKronologiskHendelseComparator implements Comparator<Behandling> {

    @Override
    public int compare(Behandling o1, Behandling o2) {
        if (o2 == null && o1 == null) {
            return 0;
        } else if (o2 == null) {
            return -1;
        } else if (o1 == null) {
            return 1;
        }
        return o2.getBehandlingDato().compareTo(o1.getBehandlingDato());
    }
}
