package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

import java.util.Comparator;

public class OmvendtKronologiskSistEndretDatoComparator implements Comparator<Soknad> {

    @Override
    public int compare(Soknad henvendelse1, Soknad henvendelse2) {
        return henvendelse2.getSistendretDato().compareTo(henvendelse1.getSistendretDato());
    }
}