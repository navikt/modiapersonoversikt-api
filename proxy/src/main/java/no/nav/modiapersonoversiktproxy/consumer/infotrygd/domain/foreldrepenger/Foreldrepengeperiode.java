package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger;

import no.nav.modiapersonoversiktproxy.commondomain.Periode;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.HistoriskUtbetaling;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.Kodeverkstype;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.KommendeUtbetaling;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Foreldrepengeperiode implements Serializable {

    private String fodselsnummer;
    private boolean harAleneomsorgFar;
    private boolean harAleneomsorgMor;
    private Double arbeidsprosentMor;
    private Kodeverkstype avslagsaarsak;
    private LocalDate avslaatt;
    private Double disponibelGradering;
    private boolean erFedrekvote;
    private Kodeverkstype forskyvelsesaarsak1;
    private Kodeverkstype forskyvelsesaarsak2;
    private Periode forskyvelsesperiode;
    private Periode forskyvelsesperiode2;
    private LocalDate foreldrepengerFom;
    private LocalDate midlertidigStansDato;
    private boolean erModrekvote;
    private Kodeverkstype morSituasjon;
    private Kodeverkstype rettTilFedrekvote;
    private Kodeverkstype rettTilModrekvote;
    private Kodeverkstype stansaarsak;
    private List<HistoriskUtbetaling> historiskeUtbetalinger;
    private List<KommendeUtbetaling> kommendeUtbetalinger;

    public Foreldrepengeperiode() {
    }

    public boolean isErModrekvote() {
        return erModrekvote;
    }

    public void setErModrekvote(boolean erModrekvote) {
        this.erModrekvote = erModrekvote;
    }

    public Kodeverkstype isRettTilModrekvote() {
        return rettTilModrekvote;
    }

    public void setRettTilModrekvote(Kodeverkstype rettTilModrekvote) {
        this.rettTilModrekvote = rettTilModrekvote;
    }

    public boolean isHarAleneomsorgFar() {
        return harAleneomsorgFar;
    }

    public void setHarAleneomsorgFar(boolean value) {
        this.harAleneomsorgFar = value;
    }

    public boolean isHarAleneomsorgMor() {
        return harAleneomsorgMor;
    }

    public void setHarAleneomsorgMor(boolean value) {
        this.harAleneomsorgMor = value;
    }

    public Double getArbeidsprosentMor() {
        return arbeidsprosentMor;
    }

    public void setArbeidsprosentMor(Double arbeidsprosentMor) {
        this.arbeidsprosentMor = arbeidsprosentMor;
    }

    public LocalDate getAvslaatt() {
        return avslaatt;
    }

    public void setAvslaatt(LocalDate value) {
        this.avslaatt = value;
    }

    public Double getDisponibelGradering() {
        return disponibelGradering;
    }

    public void setDisponibelGradering(Double value) {
        this.disponibelGradering = value;
    }

    public boolean isErFedrekvote() {
        return erFedrekvote;
    }

    public void setErFedrekvote(boolean value) {
        this.erFedrekvote = value;
    }

    public Kodeverkstype getForskyvelsesaarsak1() {
        return forskyvelsesaarsak1;
    }

    public void setForskyvelsesaarsak1(Kodeverkstype value) {
        this.forskyvelsesaarsak1 = value;
    }

    public Periode getForskyvelsesperiode() {
        return forskyvelsesperiode;
    }

    public void setForskyvelsesperiode(Periode value) {
        this.forskyvelsesperiode = value;
    }

    public LocalDate getForeldrepengerFom() {
        return foreldrepengerFom;
    }

    public void setForeldrepengerFom(LocalDate value) {
        this.foreldrepengerFom = value;
    }

    public Kodeverkstype getMorSituasjon() {
        return morSituasjon;
    }

    public void setMorSituasjon(Kodeverkstype value) {
        this.morSituasjon = value;
    }

    public Kodeverkstype getRettTilFedrekvote() {
        return rettTilFedrekvote;
    }

    public void setRettTilFedrekvote(Kodeverkstype rettTilFedrekvote) {
        this.rettTilFedrekvote = rettTilFedrekvote;
    }

    public void setHistoriskeUtbetalinger(List<HistoriskUtbetaling> historiskeUtbetalinger) {
        this.historiskeUtbetalinger = historiskeUtbetalinger;
    }

    public List<HistoriskUtbetaling> getHistoriskeUtbetalinger() {
        if (historiskeUtbetalinger == null) {
            historiskeUtbetalinger = new ArrayList<>();
        }
        return this.historiskeUtbetalinger;
    }

    public List<KommendeUtbetaling> getKommendeUtbetalinger() {
        return kommendeUtbetalinger;
    }

    public void setKommendeUtbetalinger(List<KommendeUtbetaling> kommendeUtbetalinger) {
        this.kommendeUtbetalinger = kommendeUtbetalinger;
    }

    public LocalDate getMidlertidigStansDato() {
        return midlertidigStansDato;
    }

    public void setMidlertidigStansDato(LocalDate midlertidigStansDato) {
        this.midlertidigStansDato = midlertidigStansDato;
    }

    public Kodeverkstype getStansaarsak() {
        return stansaarsak;
    }

    public void setStansaarsak(Kodeverkstype stansaarsak) {
        this.stansaarsak = stansaarsak;
    }

    public Kodeverkstype getAvslagsaarsak() {
        return avslagsaarsak;
    }

    public void setAvslagsaarsak(Kodeverkstype avslagsaarsak) {
        this.avslagsaarsak = avslagsaarsak;
    }

    public Periode getForskyvelsesperiode2() {
        return forskyvelsesperiode2;
    }

    public void setForskyvelsesperiode2(Periode forskyvelsesperiode2) {
        this.forskyvelsesperiode2 = forskyvelsesperiode2;
    }

    public Kodeverkstype getForskyvelsesaarsak2() {
        return forskyvelsesaarsak2;
    }

    public void setForskyvelsesaarsak2(Kodeverkstype forskyvelsesaarsak2) {
        this.forskyvelsesaarsak2 = forskyvelsesaarsak2;
    }

    public String getFodselsnummer() {
        return fodselsnummer;
    }

    public void setFodselsnummer(String fodselsnummer) {
        this.fodselsnummer = fodselsnummer;
    }
}
