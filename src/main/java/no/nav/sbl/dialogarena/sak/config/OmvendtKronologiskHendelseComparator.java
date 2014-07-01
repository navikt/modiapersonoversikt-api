package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling;

import java.util.Comparator;

import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BEHANDLING_DATO;

public class OmvendtKronologiskHendelseComparator implements Comparator<Record<? extends GenerellBehandling>> {

    @Override
    public int compare(Record<? extends GenerellBehandling> o1, Record<? extends GenerellBehandling> o2) {
        return o2.get(BEHANDLING_DATO).compareTo(o1.get(BEHANDLING_DATO));
    }
}
