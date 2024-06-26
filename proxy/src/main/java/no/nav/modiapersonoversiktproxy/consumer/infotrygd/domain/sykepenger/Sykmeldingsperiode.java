package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.sykepenger;

import no.nav.modiapersonoversiktproxy.commondomain.Periode;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.*;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

public class Sykmeldingsperiode extends CommonFakta implements Serializable {

    private String fodselsnummer;
    private LocalDate sykmeldtFom;
    private Integer forbrukteDager;
    private Periode ferie1;
    private Periode ferie2;
    private Periode sanksjon;
    private Kodeverkstype stansarsak;
    private Kodeverkstype unntakAktivitet;
    private Forsikring gjeldendeForsikring;
    private List<Sykmelding> sykmeldinger;
    private List<HistoriskUtbetaling> historiskeUtbetalinger;
    private List<KommendeUtbetaling> kommendeUtbetalinger;
    private List<UtbetalingPaVent> utbetalingerPaVent;
    private Bruker bruker;
    private LocalDate midlertidigStanset;
    public Sykmeldingsperiode() {
    }

    public Periode getFerie1() {
        return ferie1;
    }

    public void setFerie1(Periode ferie1) {
        this.ferie1 = ferie1;
    }

    public Integer getForbrukteDager() {
        return forbrukteDager;
    }

    public void setForbrukteDager(Integer forbrukteDager) {
        this.forbrukteDager = forbrukteDager;
    }

    public Forsikring getGjeldendeForsikring() {
        return gjeldendeForsikring;
    }

    public void setGjeldendeForsikring(Forsikring gjeldendeForsikring) {
        this.gjeldendeForsikring = gjeldendeForsikring;
    }

    public List<HistoriskUtbetaling> getHistoriskeUtbetalinger() {
        return historiskeUtbetalinger;
    }

    public void setHistoriskeUtbetalinger(List<HistoriskUtbetaling> historiskeUtbetalinger) {
        this.historiskeUtbetalinger = historiskeUtbetalinger;
    }

    public LocalDate getSykmeldtFom() {
        return sykmeldtFom;
    }

    public void setSykmeldtFom(LocalDate sykmeldtFom) {
        this.sykmeldtFom = sykmeldtFom;
    }

    public List<KommendeUtbetaling> getKommendeUtbetalinger() {
        return kommendeUtbetalinger;
    }

    public void setKommendeUtbetalinger(List<KommendeUtbetaling> kommendeUtbetalinger) {
        this.kommendeUtbetalinger = kommendeUtbetalinger;
    }

    public Kodeverkstype getStansarsak() {
        return stansarsak;
    }

    public void setStansarsak(Kodeverkstype stansarsak) {
        this.stansarsak = stansarsak;
    }

    public Kodeverkstype getUnntakAktivitet() {
        return unntakAktivitet;
    }

    public void setUnntakAktivitet(Kodeverkstype unntakAktivitet) {
        this.unntakAktivitet = unntakAktivitet;
    }

    public List<Sykmelding> getSykmeldinger() {
        return sykmeldinger;
    }

	public Sykmelding getSykmelding() {
		return sykmeldinger != null ? sykmeldinger.get(0) : null;
	}

    public void setSykmeldinger(List<Sykmelding> sykmeldinger) {
        this.sykmeldinger = sykmeldinger;
    }

    public Bruker getBruker() {
        return bruker;
    }

    public void setBruker(Bruker bruker) {
        this.bruker = bruker;
    }

    public LocalDate getMidlertidigStanset() {
        return midlertidigStanset;
    }

    public void setMidlertidigStanset(LocalDate midlertidigStanset) {
        this.midlertidigStanset = midlertidigStanset;
    }

    public Periode getFerie2() {
        return ferie2;
    }

    public void setFerie2(Periode ferie2) {
        this.ferie2 = ferie2;
    }

    public Periode getSanksjon() {
        return sanksjon;
    }

    public void setSanksjon(Periode sanksjon) {
        this.sanksjon = sanksjon;
    }

    public List<UtbetalingPaVent> getUtbetalingerPaVent() {
        return utbetalingerPaVent;
    }
    public void setUtbetalingerPaVent(List<UtbetalingPaVent> utbetalingerPaVent) {
        this.utbetalingerPaVent = utbetalingerPaVent;
    }

    public String getFodselsnummer() {
        return fodselsnummer;
    }

    public void setFodselsnummer(String fodselsnummer) {
        this.fodselsnummer = fodselsnummer;
    }
}
