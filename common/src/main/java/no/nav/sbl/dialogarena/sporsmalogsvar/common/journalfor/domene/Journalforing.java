package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.utils.Utils.ARKIVTEMA;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.utils.Utils.SORTER_NYESTE_OVERST;

public class Journalforing implements Serializable {

    private final Traad traad;
    private Map<String, List<Sak>> sakerPerTema;
    private Optional<Sak> valgtSak;
    private Boolean sensitiv;

    public Journalforing(Traad traad, Iterable<Sak> saker) {
        this.traad = traad;
        sakerPerTema = new HashMap<>();
        for (String temakode : on(saker).map(ARKIVTEMA).collectIn(new HashSet<String>())) {
            sakerPerTema.put(temakode, on(saker).filter(where(ARKIVTEMA, equalTo(temakode))).collect(SORTER_NYESTE_OVERST));
        }
        valgtSak = none();
        sensitiv = traad.erSensitiv;
    }

    public List<String> getTemakoder() {
        return on(sakerPerTema.keySet()).collect(String.CASE_INSENSITIVE_ORDER);
    }

    public List<Sak> getSaker(String temakode) {
        return sakerPerTema.get(temakode);
    }

    public Sak getValgtSak() {
        return valgtSak.getOrElse(null);
    }

    public void setValgtSak(Sak sak) {
        valgtSak = optional(sak);
    }

    public Boolean isSensitiv() {
        return sensitiv;
    }

    public void setSensitiv(Boolean sensitiv) {
        this.sensitiv = sensitiv;
    }
}
