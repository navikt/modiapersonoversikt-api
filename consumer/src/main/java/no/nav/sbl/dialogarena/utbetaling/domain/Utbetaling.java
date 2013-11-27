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

import static org.apache.commons.lang3.StringUtils.join;
import static org.joda.time.DateTime.now;

public class Utbetaling implements Serializable {

    public static final LocalDate DEFAULT_STARTDATO = now().minusMonths(3).toLocalDate();
    public static final LocalDate DEFAULT_SLUTTDATO = now().toLocalDate();
    private final String utbetalingId;
    private String fnr;
    private List<Bilag> bilag = new ArrayList<>();
    private String statuskode;
    private DateTime utbetalingsDato;
    private double bruttoBelop;
    private double nettoBelop;
    private double trekk;
    private String valuta;
    private String kontoNr;
    private Mottaker mottaker;
    private Periode periode;

    //CHECKSTYLE:OFF
    Utbetaling(String fnr, List<Bilag> bilag, String statuskode, DateTime utbetalingsDato, double bruttoBelop, double nettoBelop, String valuta, String kontoNr, String utbetalingId, Mottaker mottaker, Periode periode, double trekk) {
        this.fnr = fnr;
        this.bilag = bilag;
        this.statuskode = statuskode;
        this.utbetalingsDato = utbetalingsDato;
        this.bruttoBelop = bruttoBelop;
        this.nettoBelop = nettoBelop;
        this.valuta = valuta;
        this.kontoNr = kontoNr;
        this.utbetalingId = utbetalingId;
        this.mottaker = mottaker;
        this.periode = periode;
        this.trekk = trekk;
    }
    //CHECKSTYLE:ON

    public Utbetaling(String fnr, WSUtbetaling wsUtbetaling) {
        for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
            bilag.add(new Bilag(wsBilag));
        }
        this.fnr = fnr;
        this.statuskode = wsUtbetaling.getStatusKode();
        this.utbetalingsDato = wsUtbetaling.getUtbetalingDato();
        this.bruttoBelop = wsUtbetaling.getBruttobelop();
        this.nettoBelop = wsUtbetaling.getNettobelop();
        this.trekk = wsUtbetaling.getTrekk();
        this.kontoNr = join(getKontoNrFromBilag(), ", ");
        this.utbetalingId = wsUtbetaling.getUtbetalingId();
        this.valuta = transformValuta(wsUtbetaling.getValuta());
        this.mottaker = new Mottaker(fnr, wsUtbetaling.getUtbetalingMottaker());
        this.periode = new Periode(wsUtbetaling.getUtbetalingsPeriode());
    }


    public String getFnr() {
        return fnr;
    }

    public Mottaker getMottaker() {
        return mottaker;
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

    public String getStatuskode() {
        return statuskode;
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

    protected Set<String> getBeskrivelser() {
        Set<String> beskrivelser = new TreeSet<>();
        for (Bilag detalj : bilag) {
            beskrivelser.addAll(detalj.getBeskrivelserFromDetaljer());
        }
        return beskrivelser;
    }

    private String transformValuta(String wsValuta) {
        return (wsValuta == null || wsValuta.isEmpty()) ? "kr" : wsValuta;
    }

    private Set<String> getKontoNrFromBilag() {
        Set<String> kontoNrSet = new TreeSet<>();
        for (Bilag detalj : bilag) {
            kontoNrSet.addAll(detalj.getKontoNrFromDetaljer());
        }
        return kontoNrSet;
    }

}
