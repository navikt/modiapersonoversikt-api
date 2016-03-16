package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;

import java.util.Comparator;

public class NyesteOppdaterteTemaComparator implements Comparator<Tema> {

    @Override
    public int compare(Tema o1, Tema o2) {
        Behandling sistoppdatertebehandling1 = o1.getSistoppdatertebehandling();
        Behandling sistoppdatertebehandling2 = o2.getSistoppdatertebehandling();
        return sistoppdatertebehandling1.getBehandlingDato().compareTo(sistoppdatertebehandling2.getBehandlingDato());
    }
}
