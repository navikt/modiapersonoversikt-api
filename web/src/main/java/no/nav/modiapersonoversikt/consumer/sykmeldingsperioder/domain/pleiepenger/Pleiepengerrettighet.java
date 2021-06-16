package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Pleiepengerrettighet implements Serializable {

    private final List<Pleiepengeperiode> perioder;
    private String barnet;
    private String omsorgsperson;
    private String andreOmsorgsperson;
    private int restDagerFOMIMorgen;
    private int forbrukteDagerTOMIDag;
    private int pleiepengedager;
    private int restDagerAnvist;

    public Pleiepengerrettighet() {
        perioder = new ArrayList<>();
    }

    public Optional<Pleiepengeperiode> getAktuellPleiepengeperiode() {
        Optional<Pleiepengeperiode> gjeldendeEllerNeste = getGjeldendeEllerNestePeriode();
        return gjeldendeEllerNeste.isPresent() ? gjeldendeEllerNeste : getSistePleiepengeperiode();
    }

    private Optional<Pleiepengeperiode> getGjeldendeEllerNestePeriode() {
        return perioder.stream()
                .sorted(comparing(Pleiepengeperiode::getFraOgMed))
                .filter(periode -> periode.getVedtakFOMInnevaerende().findFirst().isPresent())
                .findFirst();
    }

    public Optional<Pleiepengeperiode> getSistePleiepengeperiode() {
        return perioder.stream()
                .sorted(comparing(Pleiepengeperiode::getFraOgMed).reversed())
                .findFirst();
    }

    public List<Arbeidsforhold> getAlleArbeidsforhold() {
        return perioder
                .stream()
                .flatMap(periode -> periode.getArbeidsforholdListe()
                        .stream())
                .collect(toList());
    }

    public Optional<LocalDate> getEldsteIdDato() {
        return perioder.stream()
                .map(Pleiepengeperiode::getFraOgMed)
                .sorted()
                .findFirst();
    }

    public List<Pleiepengeperiode> getPerioder() {
        return perioder;
    }

    public String getOmsorgsperson() {
        return omsorgsperson;
    }

    public int getRestDagerFOMIMorgen() {
        return restDagerFOMIMorgen;
    }

    public int getPleiepengedager() {
        return pleiepengedager;
    }

    public int getForbrukteDagerTOMIDag() {
        return forbrukteDagerTOMIDag;
    }

    public String getAndreOmsorgsperson() {
        return andreOmsorgsperson;
    }

    public String getBarnet() {
        return barnet;
    }

    public Pleiepengerrettighet withPerioder(List<Pleiepengeperiode> periode) {
        this.perioder.clear();
        this.perioder.addAll(periode);
        return this;
    }

    public Pleiepengerrettighet withOmsorgsperson(String ident) {
        this.omsorgsperson = ident;
        return this;
    }

    public Pleiepengerrettighet withAndreOmsorgsperson(String ident) {
        this.andreOmsorgsperson = ident;
        return this;
    }

    public Pleiepengerrettighet withForbrukteDagerTOMIDag(int dager) {
        this.forbrukteDagerTOMIDag = dager;
        return this;
    }

    public Pleiepengerrettighet withRestDagerFOMIMorgen(int dager) {
        this.restDagerFOMIMorgen = dager;
        return this;
    }

    public Pleiepengerrettighet withRestDagerAnvist(int dager) {
        this.restDagerAnvist = dager;
        return this;
    }

    public Pleiepengerrettighet withPleiepengedager(int dager) {
        this.pleiepengedager = dager;
        return this;
    }

    public Pleiepengerrettighet withBarnet(String ident) {
        this.barnet = ident;
        return this;
    }

    public int getRestDagerAnvist() {
        return restDagerAnvist;
    }

    public int getTotaltDagerInnvilget() {
        return pleiepengedager - restDagerAnvist;
    }

}
