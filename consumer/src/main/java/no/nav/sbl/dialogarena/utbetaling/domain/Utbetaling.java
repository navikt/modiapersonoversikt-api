package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static org.apache.commons.lang3.StringUtils.join;

public class Utbetaling implements Serializable {

    public static final String BRUKER = "bruker";
    public static final String ARBEIDSGIVER = "arbeidsgiver";

    public static LocalDate defaultStartDato() {
        return LocalDate.now().minusMonths(3);
    }
    public static LocalDate defaultSluttDato() {
        return LocalDate.now();
    }

    public final String utbetalingId;
    public String fnr;
    public List<Bilag> bilag = new ArrayList<>();
    public String statusBeskrivelse;
    public DateTime utbetalingsDato;
    public double bruttoBelop;
    public double nettoBelop;
    public double trekk;
    public String valuta;
    public String kontoNr;
    public String mottakertype;
    public String mottakernavn;
    public DateTime startDato;
    public DateTime sluttDato;

    public Utbetaling(String utbetalingId) {
        this.utbetalingId = utbetalingId;
    }

    public Utbetaling(String fnr, WSUtbetaling wsUtbetaling) {
        for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
            bilag.add(new Bilag(wsBilag));
        }
        this.fnr = fnr;
        this.statusBeskrivelse = wsUtbetaling.getStatusBeskrivelse();
        this.utbetalingsDato = wsUtbetaling.getUtbetalingDato();
        this.bruttoBelop = wsUtbetaling.getBruttobelop();
        this.nettoBelop = wsUtbetaling.getNettobelop();
        this.trekk = wsUtbetaling.getTrekk();
        this.kontoNr = join(getKontoNrFromBilag(), ", ");
        this.utbetalingId = wsUtbetaling.getUtbetalingId();
        this.valuta = transformValuta(wsUtbetaling.getValuta());
        this.mottakernavn =  wsUtbetaling.getUtbetalingMottaker().getNavn();
        this.mottakertype = fnr.equals(wsUtbetaling.getUtbetalingMottaker().getMottakerId()) ? BRUKER : ARBEIDSGIVER;
        this.startDato = wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato();
        this.sluttDato = wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato();
    }

    public String getKontoNr() {
        return kontoNr;
    }

    public String getValuta() {
        return valuta;
    }

    public List<Bilag> getBilag() {
        return bilag;
    }

    public String getBeskrivelse() {
        return join(getBeskrivelser(), ", ");
    }

    public DateTime getStartDate() {
        return startDato;
    }

    public DateTime getEndDate() {
        return sluttDato;
    }

    public String getStatusBeskrivelse() {
        return statusBeskrivelse;
    }

    public DateTime getUtbetalingsDato() {
        return utbetalingsDato;
    }

    public String getUtbetalingId() {
        return utbetalingId;
    }

    public double getTrekk() {
        return trekk;
    }

    public String getKortUtbetalingsDato() {
        return optional(utbetalingsDato).map(KORT).getOrElse("Ingen utbetalingsdato");
    }

    public String getPeriodeMedKortDato() {
        return optional(startDato).map(KORT).getOrElse("") + " - " + optional(sluttDato).map(KORT).getOrElse("");
    }

    public String getBruttoBelopMedValuta() {
        return getBelopString(bruttoBelop);
    }

    public String getTrekkMedValuta() {
        return getBelopString(trekk);
    }

    public String getBelopMedValuta() {
        return getBelopString(nettoBelop);
    }

    public boolean harYtelse(String ytelse) {
        return getBeskrivelser().contains(ytelse);
    }

    public Set<String> getBeskrivelser() {
        Set<String> beskrivelser = new TreeSet<>();
        for (Bilag detalj : bilag) {
            beskrivelser.addAll(detalj.getBeskrivelserFromDetaljer());
        }
        return beskrivelser;
    }

    private String transformValuta(String wsValuta) {
        return (wsValuta == null || wsValuta.isEmpty()) ? "NOK" : wsValuta;
    }

    private Set<String> getKontoNrFromBilag() {
        Set<String> kontoNrSet = new TreeSet<>();
        for (Bilag detalj : bilag) {
            kontoNrSet.addAll(detalj.getKontoNrFromDetaljer());
        }
        return kontoNrSet;
    }

}
