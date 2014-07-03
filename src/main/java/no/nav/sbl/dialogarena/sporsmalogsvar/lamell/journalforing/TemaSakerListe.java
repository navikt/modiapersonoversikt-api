package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker.TEMAGRUPPE;

public class TemaSakerListe extends ArrayList<TemaSaker> {

    public TemaSakerListe(List<TemaSaker> saker) {
        super(saker);
    }

    public List<TemaSaker> sorter(String valgtTraadSinTemagruppe) {
        List<TemaSaker> temaSaker = new ArrayList<>(this);
        sorterDatoInnenforSammeTema(temaSaker);

        List<TemaSaker> valgteTemaSaker = new ArrayList<>(on(temaSaker).filter(where(TEMAGRUPPE, equalTo(valgtTraadSinTemagruppe))).collect());
        temaSaker.removeAll(valgteTemaSaker);

        Collections.sort(valgteTemaSaker);
        Collections.sort(temaSaker);

        List<TemaSaker> alleTemaSaker = leggSammenValgteTemaSakerMedResterendeTemaSaker(valgteTemaSaker, temaSaker);
        sorterDatoInnenforSammeTema(alleTemaSaker);
        return alleTemaSaker;
    }

    private List<TemaSaker> leggSammenValgteTemaSakerMedResterendeTemaSaker(List<TemaSaker> valgteTemaSaker, List<TemaSaker> temaSaker) {
        valgteTemaSaker.addAll(temaSaker);
        return valgteTemaSaker;
    }

    private void sorterDatoInnenforSammeTema(List<TemaSaker> alleTemaSaker) {
        for (TemaSaker temaSaker : alleTemaSaker) {
            Collections.sort(temaSaker.saksliste);
        }
    }

}
