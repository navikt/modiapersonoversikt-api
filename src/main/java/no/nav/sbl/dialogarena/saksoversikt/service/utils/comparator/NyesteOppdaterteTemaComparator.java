package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;

import java.util.Comparator;

public class NyesteOppdaterteTemaComparator implements Comparator<Record<? extends Tema>> {

    @Override
    public int compare(Record<? extends Tema> o1, Record<? extends Tema> o2) {
        Record<? extends GenerellBehandling> siste1 = o1.get(Tema.SISTOPPDATERTEBEHANDLING);
        Record<? extends GenerellBehandling> siste2 = o2.get(Tema.SISTOPPDATERTEBEHANDLING);
        return siste2.get(GenerellBehandling.BEHANDLING_DATO).compareTo(siste1.get(GenerellBehandling.BEHANDLING_DATO));

    }
}
