package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.JOURNALFORT_DATO;

public class TraadVM implements Serializable {

    private List<MeldingVM> meldinger;

    public Sak journalfortSak;

    public TraadVM(List<MeldingVM> meldinger) {
        Map<DateTime, List<MeldingVM>> mapMeldingVMPaJournalfortDato = on(meldinger).reduce(indexBy(JOURNALFORT_DATO));

        for (Map.Entry<DateTime, List<MeldingVM>> journalfortDatoEntry : mapMeldingVMPaJournalfortDato.entrySet()) {
            if (journalfortDatoEntry.getKey() != null) {
                journalfortDatoEntry.getValue().get(0).nyesteMeldingISinJournalfortgruppe = true;
            }
        }

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

    public String getNyesteMeldingsTemagruppe() {
        return getNyesteMelding().melding.temagruppe;
    }

    public int getTraadLengde() {
        return meldinger.size();
    }

    public boolean erBehandlet() {
        return meldinger.size() > 1 || getEldsteMelding().melding.meldingstype.equals(SAMTALEREFERAT);
    }

    public boolean bleInitiertAvBruker() {
        return getEldsteMelding().melding.meldingstype == SPORSMAL;
    }

}
