package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain;

import org.joda.time.LocalDate;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class HistoriskUtbetaling extends Utbetaling {
    private LocalDate utbetalingsdato;
    private Double nettobelop;
    private Double bruttobeloep;
    private Double skattetrekk;
    private List<Kreditortrekk> trekk;
    private String arbeidsgiverNavn;
    private String arbeidsgiverOrgNr;
    private Double dagsats;
    private String type;

    public HistoriskUtbetaling() {
    }

    @SuppressWarnings("IncompleteCopyConstructor")
    public HistoriskUtbetaling(HistoriskUtbetaling other) {
        this.utbetalingsdato = other.utbetalingsdato;
        this.nettobelop = other.nettobelop;
        this.bruttobeloep = other.bruttobeloep;
        this.skattetrekk = other.skattetrekk;
        this.trekk = other.trekk == null ? null :
                other.trekk.stream()
                        .map(trekk -> new Kreditortrekk(trekk))
                        .collect(toList());
        this.arbeidsgiverNavn = other.arbeidsgiverNavn;
        this.arbeidsgiverOrgNr = other.arbeidsgiverOrgNr;
        this.dagsats = other.dagsats;
        this.type = other.type;
    }

    public Double getBruttobeloep() {
        return bruttobeloep;
    }

    public void setBruttobeloep(Double bruttobeloep) {
        this.bruttobeloep = bruttobeloep;
    }

    public Double getNettobelop() {
        return nettobelop;
    }

    public void setNettobelop(Double nettobelop) {
        this.nettobelop = nettobelop;
    }

    public Double getSkattetrekk() {
        return skattetrekk;
    }

    public void setSkattetrekk(Double skattetrekk) {
        this.skattetrekk = skattetrekk;
    }

    public LocalDate getUtbetalingsdato() {
        return utbetalingsdato;
    }

    public void setUtbetalingsdato(LocalDate utbetalingsdato) {
        this.utbetalingsdato = utbetalingsdato;
    }

    public List<Kreditortrekk> getTrekk() {
        return trekk;
    }

    public void setTrekk(List<Kreditortrekk> trekk) {
        this.trekk = trekk;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getArbeidsgiverOrgNr() {
        return arbeidsgiverOrgNr;
    }

    public void setArbeidsgiverOrgNr(String arbeidsgiverOrgNr) {
        this.arbeidsgiverOrgNr = arbeidsgiverOrgNr;
    }

    public Double getDagsats() {
        return dagsats;
    }

    public void setDagsats(Double dagsats) {
        this.dagsats = dagsats;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public HistoriskUtbetaling withNettoBelop(double nettobelop) {
        setNettobelop(nettobelop);
        return this;
    }

    public HistoriskUtbetaling withSkattetrekk(double skattetrekk) {
        setSkattetrekk(skattetrekk);
        return this;
    }

    public HistoriskUtbetaling withTrekk(List<Kreditortrekk> trekk) {
        setTrekk(trekk);
        return this;
    }

    public HistoriskUtbetaling withArbeidsgiverNavn(String arbeidsgiverNavn) {
        setArbeidsgiverNavn(arbeidsgiverNavn);
        return this;
    }

    public HistoriskUtbetaling withArbeidsgiverOrgnr(String arbeidsgiverOrgNr) {
        setArbeidsgiverOrgNr(arbeidsgiverOrgNr);
        return this;
    }

    public HistoriskUtbetaling withDagsats(double dagsats) {
        setDagsats(dagsats);
        return this;
    }

    public HistoriskUtbetaling withYtelsesType(String ytelsesType) {
        setType(ytelsesType);
        return this;
    }

    public HistoriskUtbetaling withBruttoBelop(double bruttoBelop) {
        setBruttobeloep(bruttoBelop);
        return this;
    }

    public HistoriskUtbetaling withUtbetalingsdato(final LocalDate utbetalingsdato) {
        setUtbetalingsdato(utbetalingsdato);
        return this;
    }
}
