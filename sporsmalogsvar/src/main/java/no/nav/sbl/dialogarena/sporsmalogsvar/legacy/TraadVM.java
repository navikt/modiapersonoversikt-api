package no.nav.sbl.dialogarena.sporsmalogsvar.legacy;


import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.VARSEL;


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
        if (meldinger.isEmpty()) {
            return null;
        }
        return meldinger.get(meldinger.size() - 1);
    }

    public List<MeldingVM> getTidligereMeldinger() {
        return meldinger.isEmpty() ? new ArrayList<>() : meldinger.subList(1, meldinger.size());
    }

    public String getNyesteMeldingsTemagruppe() {
        return getNyesteMelding().melding.temagruppe;
    }

    public int getTraadLengde() {
        return meldinger.size();
    }

    public boolean erBehandlet() {
        return minstEnMeldingErFraNav() || erFerdigstiltUtenSvar();
    }

    private boolean minstEnMeldingErFraNav() {
        return meldinger.stream()
                .filter(melding -> !melding.erDelsvar())
                .anyMatch((meldingVM) -> meldingVM.melding.erFraSaksbehandler());
    }

    public boolean erKontorsperret() {
        return getKontorsperretEnhet().isPresent();
    }

    public Optional<String> getKontorsperretEnhet() {
        if (meldinger.isEmpty()) {
            return Optional.empty();
        }

        return ofNullable(getEldsteMelding().melding.kontorsperretEnhet);
    }

    public boolean erFeilsendt() {
        return meldinger.stream().anyMatch(MeldingVM::erFeilsendt);
    }

    public boolean erMonolog() {
        return meldinger.stream()
                .map(MeldingVM::erFraSaksbehandler)
                .distinct()
                .count()
                < 2;
    }

    public boolean erVarsel() {
        return VARSEL.contains(getEldsteMelding().getMeldingstype());
    }

    public boolean erTemagruppeSosialeTjenester() {
        Temagruppe gjeldendeTemagruppe = getEldsteMelding().melding.gjeldendeTemagruppe;
        return asList(Temagruppe.OKSOS, Temagruppe.ANSOS).contains(gjeldendeTemagruppe);
    }

    public boolean erJournalfort() {
        return getEldsteMelding().isJournalfort();
    }

    public boolean erFerdigstiltUtenSvar() {
        return getEldsteMelding().erFerdigstiltUtenSvar();
    }

    private boolean erEnkeltstaaendeSpsmFraBruker() {
        return meldinger.size() == 1 && erMeldingstypeSporsmal();
    }

    public boolean erMeldingstypeSporsmal() {
        return getEldsteMelding().getMeldingstype() == Meldingstype.SPORSMAL_SKRIFTLIG
                || getEldsteMelding().getMeldingstype() == Meldingstype.SPORSMAL_SKRIFTLIG_DIREKTE;
    }

    public boolean erMeldingstypeSamtalereferart() {
        return getEldsteMelding().getMeldingstype() == Meldingstype.SAMTALEREFERAT_OPPMOTE
                || getEldsteMelding().getMeldingstype() == Meldingstype.SAMTALEREFERAT_TELEFON;
    }

    public boolean erSisteMeldingEtDelsvar() {
        return getNyesteMelding().erDelsvar();
    }

    public boolean harDelsvar() {
        return meldinger.stream().anyMatch(MeldingVM::erDelsvar);
    }

    public Optional<DateTime> getFerdigstiltUtenSvarDato() {
        return getEldsteMelding().getFerdigstiltUtenSvarDato();
    }

    public Optional<Saksbehandler> getFerdigstiltUtenSvarAv() {
        return getEldsteMelding().getFerdigstiltUtenSvarAv();
    }

    public Optional<DateTime> getKontorsperretDato() {
        return getEldsteMelding().getKontorsperretDato();
    }

    public Optional<Saksbehandler> getKontorsperretAv() {
        return getEldsteMelding().getKontorsperretAv();
    }

}
