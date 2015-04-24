package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.containedIn;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.hentTemaForTemagruppe;

public class SakerListe extends ArrayList<SakerForTema> {

    public SakerListe(List<SakerForTema> saker) {
        super(saker);
    }

    public List<SakerForTema> sorter() {
        Collections.sort(this);
        return this;
    }

    public List<SakerForTema> sorter(String valgtTraadSinTemagruppe) {
        List<SakerForTema> sakerForTema = new ArrayList<>(this);

        List<String> prioriterteTemakoder = hentTemaForTemagruppe(valgtTraadSinTemagruppe);
        List<SakerForTema> valgteSakerForTema = on(sakerForTema)
                .filter(where(SakerForTema.TEMA_KODE, containedIn(prioriterteTemakoder)))
                .collectIn(new ArrayList<SakerForTema>());

        sakerForTema.removeAll(valgteSakerForTema);

        Collections.sort(valgteSakerForTema);
        Collections.sort(sakerForTema);

        return leggSammenValgteTemaSakerMedResterendeTemaSaker(valgteSakerForTema, sakerForTema);
    }

    private List<SakerForTema> leggSammenValgteTemaSakerMedResterendeTemaSaker(List<SakerForTema> valgteSakerForTema, List<SakerForTema> sakerForTema) {
        valgteSakerForTema.addAll(sakerForTema);
        return valgteSakerForTema;
    }
}
