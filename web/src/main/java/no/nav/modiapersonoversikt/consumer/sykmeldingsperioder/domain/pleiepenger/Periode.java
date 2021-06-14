package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger;

import java.io.Serializable;
import java.time.LocalDate;

public class Periode implements Serializable {

    public final LocalDate fraOgMed;
    public final LocalDate tilOgMed;

    public Periode(LocalDate fraOgMed, LocalDate tilOgMed) {
        this.fraOgMed = fraOgMed;
        this.tilOgMed = tilOgMed;
    }

    public boolean erGjeldende() {
        LocalDate today = LocalDate.now();
        return ((fraOgMed.isEqual(today) || fraOgMed.isBefore(today)) &&
                (tilOgMed.isEqual(today) || tilOgMed.isAfter(today)));
    }

    public boolean erGjeldendeEllerSenere() {
        return erGjeldende() || fraOgMed.isAfter(LocalDate.now());
    }

}
