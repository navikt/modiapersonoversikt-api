package no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;

public class Pleiepengeperiode implements Serializable {

    private final List<Arbeidsforhold> arbeidsforholdListe;
    private final List<Vedtak> vedtakListe;

    private LocalDate fraOgMed;
    private int antallPleiepengedager;
    private String arbeidskategori;

    public Pleiepengeperiode() {
        vedtakListe = new ArrayList<>();
        arbeidsforholdListe = new ArrayList<>();
    }

    private Stream<Vedtak> getSorterteVedtak() {
        return vedtakListe.stream()
                .sorted(comparing((Vedtak vedtak) -> vedtak.getPeriode().tilOgMed).reversed());
    }

    public Stream<Vedtak> getVedtakFOMInnevaerende() {
        return vedtakListe.stream()
                .sorted(comparing((Vedtak vedtak) -> vedtak.getPeriode().tilOgMed))
                .filter(vedtak -> vedtak.getPeriode().erGjeldendeEllerSenere());
    }

    public Optional<Vedtak> getAktueltVedtak() {
        Vedtak aktueltVedtak = getVedtakFOMInnevaerende()
                .findFirst()
                .orElseGet(() -> getNyesteVedtak().orElse(null));
        return ofNullable(aktueltVedtak);
    }

    public Optional<Vedtak> getNyesteVedtak() {
        return vedtakListe.stream()
                .sorted(comparing((Vedtak vedtak) -> vedtak.getPeriode().tilOgMed).reversed())
                .findFirst();
    }

    public LocalDate getFraOgMed() {
        return fraOgMed;
    }

    public Pleiepengeperiode withFraOgMed(LocalDate fraOgMed) {
        this.fraOgMed = fraOgMed;
        return this;
    }

    public List<Vedtak> getVedtakListe() {
        return vedtakListe;
    }

    public Pleiepengeperiode withVedtakListe(List<Vedtak> vedtakListe) {
        if (vedtakListe == null) {
            throw new IllegalArgumentException();
        }
        this.vedtakListe.clear();
        this.vedtakListe.addAll(vedtakListe);
        return this;
    }

    public List<Arbeidsforhold> getArbeidsforholdListe() {
        return arbeidsforholdListe;
    }

    public Pleiepengeperiode withArbeidsforholdListe(List<Arbeidsforhold> arbeidsforholdListe) {
        if (arbeidsforholdListe == null) {
            throw new IllegalArgumentException();
        }

        this.arbeidsforholdListe.clear();
        this.arbeidsforholdListe.addAll(arbeidsforholdListe);
        return this;
    }

    public int getAntallPleiepengedager() {
        return antallPleiepengedager;
    }

    public Pleiepengeperiode withAntallPleiepengedager(int antallPleiepengedager) {
        this.antallPleiepengedager = antallPleiepengedager;
        return this;
    }

    public Pleiepengeperiode withArbeidskategori(String arbeidskategori) {
        this.arbeidskategori = arbeidskategori;
        return this;
    }
}
