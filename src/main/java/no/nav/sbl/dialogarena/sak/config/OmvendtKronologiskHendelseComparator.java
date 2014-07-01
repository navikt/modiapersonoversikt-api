package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;

import java.util.Comparator;

public class OmvendtKronologiskHendelseComparator implements Comparator<GenerellBehandling> {

    @Override
    public int compare(GenerellBehandling o1, GenerellBehandling o2) {
        return o2.behandlingDato.compareTo(o1.behandlingDato);
    }
}
