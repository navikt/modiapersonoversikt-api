package no.nav.sbl.dialogarena.utbetaling.domain;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.summerMapVerdier;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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
    public Periode periode;

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
        this.periode = new Periode(wsUtbetaling.getUtbetalingsPeriode());
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
        return periode != null ? periode.getStartDato() : null;
    }

    public DateTime getEndDate() {
        return periode != null ? periode.getSluttDato() : null;
    }

    public Periode getPeriode() {
        return periode;
    }

    public String getStatusBeskrivelse() {
        return statusBeskrivelse;
    }

    public DateTime getUtbetalingsDato() {
        return utbetalingsDato;
    }

    public double getBruttoBelop() {
        return bruttoBelop;
    }

    public String getUtbetalingId() {
        return utbetalingId;
    }

    public double getNettoBelop() {
        return nettoBelop;
    }

    public double getTrekk() {
        return trekk;
    }

    public String getKortUtbetalingsDato() {
        return optional(utbetalingsDato).map(KORT).getOrElse("Ingen utbetalingsdato");
    }

    public String getPeriodeMedKortDato() {
        return optional(periode.getStartDato()).map(KORT).getOrElse("") + " - " + optional(periode.getSluttDato()).map(KORT).getOrElse("");
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

    public Map<String, Double> getBelopPerYtelser() {
        Map<String, Double> oppsummert = new HashMap<>();
        for (Bilag bilag1 : bilag) {
            Map<String, Double> belopPerYtelse = bilag1.getBelopPerYtelse();
            summerMapVerdier(oppsummert, belopPerYtelse);
        }
        return oppsummert;
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
