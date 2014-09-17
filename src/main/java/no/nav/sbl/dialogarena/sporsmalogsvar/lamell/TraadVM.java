package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.JOURNALFORT_DATO;

public class TraadVM implements Serializable {

    private List<MeldingVM> meldinger;

    public Sak journalfortSak;

    public TraadVM(List<MeldingVM> meldinger) {
        this.meldinger = grupperMeldingerPaaJournalfortdato(meldinger);
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
        return meldinger.size() > 1 || SAMTALEREFERAT.contains(getEldsteMelding().melding.meldingstype);
    }

    public boolean erKontorsperret() {
        return getKontorsperretEnhet().isSome();
    }

    public Optional<String> getKontorsperretEnhet() {
        return optional(meldinger.get(0).melding.kontorsperretEnhet);
    }

    public boolean erFeilsendt() {
        return getMarkertSomFeilsendtAv().isSome();
    }

    public Optional<String> getMarkertSomFeilsendtAv() {
        return optional(meldinger.get(0).melding.markertSomFeilsendtAv);
    }

    public boolean bleInitiertAvBruker() {
        return getEldsteMelding().melding.meldingstype == SPORSMAL_SKRIFTLIG;
    }

    public static List<MeldingVM> grupperMeldingerPaaJournalfortdato(List<MeldingVM> meldinger) {
        Map<LocalDate, List<MeldingVM>> mapMeldingVMPaJournalfortDato = on(meldinger).reduce(indexBy(JOURNALFORT_DATO));

        for (Entry<LocalDate, List<MeldingVM>> journalfortDatoEntry : mapMeldingVMPaJournalfortDato.entrySet()) {
            if (journalfortDatoEntry.getKey() != null) {
                journalfortDatoEntry.getValue().get(0).nyesteMeldingISinJournalfortgruppe = true;
            }
        }

        return meldinger;
    }

}
