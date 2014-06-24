package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Sak;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.SPORSMAL;

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
        return meldinger.get(0);
    }

    public MeldingVM getEldsteMelding() {
        return meldinger.get(meldinger.size() - 1);
    }

    public List<MeldingVM> getTidligereMeldinger() {
        return meldinger.isEmpty() ? new ArrayList<MeldingVM>() : meldinger.subList(1, meldinger.size());
    }

    public String getNyesteMeldingsTema() {
        return getNyesteMelding().melding.tema;
    }

    public int getTraadLengde() {
        return meldinger.size();
    }

    public boolean bleInitiertAvBruker() {
        return getEldsteMelding() != null && getEldsteMelding().melding.meldingstype == SPORSMAL;
    }

}
