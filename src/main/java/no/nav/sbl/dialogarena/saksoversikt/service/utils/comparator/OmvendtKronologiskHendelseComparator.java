package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;

import java.util.Comparator;

public class OmvendtKronologiskHendelseComparator implements Comparator<Record<? extends GenerellBehandling>> {

    @Override
    public int compare(Record<? extends GenerellBehandling> o1, Record<? extends GenerellBehandling> o2) {
        if (o2 == null && o1 == null) {
            return 0;
        } else if (o2 == null) {
            return -1;
        } else if (o1 == null) {
            return 1;
        }
        return o2.get(GenerellBehandling.BEHANDLING_DATO).compareTo(o1.get(GenerellBehandling.BEHANDLING_DATO));
    }
}
