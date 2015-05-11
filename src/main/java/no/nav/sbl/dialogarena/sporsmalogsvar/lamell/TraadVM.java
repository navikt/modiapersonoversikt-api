package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.FRA_NAV;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.FEILSENDT;

public class TraadVM implements Serializable {

    private List<MeldingVM> meldinger;

    public Sak journalfortSak;

    public TraadVM(List<MeldingVM> meldinger) {
        this.meldinger = meldinger;
    }

    public List<MeldingVM> getMeldinger() {
        return meldinger;
    }

    public MeldingVM getNyesteMelding() {
        if (meldinger.isEmpty()) {
            return null;
        }
        return meldinger.get(0);
    }

    public MeldingVM getEldsteMelding() {
        return meldinger.get(meldinger.size() - 1);
    }

    public List<MeldingVM> getTidligereMeldinger() {
        return meldinger.isEmpty() ? new ArrayList<MeldingVM>() : meldinger.subList(1, meldinger.size());
    }

    public String getNyesteMeldingsTemagruppe() {
        return getNyesteMelding().melding.temagruppe;
    }

    public int getTraadLengde() {
        return meldinger.size();
    }

    public boolean erBehandlet() {
        return meldinger.size() > 1 || FRA_NAV.contains(getEldsteMelding().melding.meldingstype);
    }

    public boolean erKontorsperret() {
        return getKontorsperretEnhet().isSome();
    }

    public Optional<String> getKontorsperretEnhet() {
        return optional(meldinger.get(0).melding.kontorsperretEnhet);
    }

    public boolean erFeilsendt() {
        return !on(meldinger).filter(where(FEILSENDT, equalTo(true))).isEmpty();
    }

    public boolean traadKanBesvares() {
        return SPORSMAL.contains(getEldsteMelding().melding.meldingstype) &&
                !getEldsteMelding().melding.kassert
                && !getEldsteMelding().erKontorsperret()
                && !getEldsteMelding().erFeilsendt();
    }

}
