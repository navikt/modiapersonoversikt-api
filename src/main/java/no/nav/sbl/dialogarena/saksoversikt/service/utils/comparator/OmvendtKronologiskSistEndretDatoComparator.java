package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

import java.util.Comparator;

public class OmvendtKronologiskSistEndretDatoComparator implements Comparator<Record<Soknad>> {

    @Override
    public int compare(Record<Soknad> henvendelse1, Record<Soknad> henvendelse2) {
        return henvendelse2.get(Soknad.SISTENDRET_DATO).compareTo(henvendelse1.get(Soknad.SISTENDRET_DATO));
    }
}