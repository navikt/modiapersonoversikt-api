package no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain;

import java.util.*;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class SakerListe extends ArrayList<SakerForTema> {

    public SakerListe(List<SakerForTema> saker) {
        super(saker);
    }

    public List<SakerForTema> sorter(String valgtTraadSinTemagruppe) {
        List<SakerForTema> sakerForTema = new ArrayList<>(this);

        List<SakerForTema> valgteSakerForTema = new ArrayList<>(
                on(sakerForTema)
                .filter(where(SakerForTema.TEMAGRUPPE, equalTo(valgtTraadSinTemagruppe)))
                .collect()
        );
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
