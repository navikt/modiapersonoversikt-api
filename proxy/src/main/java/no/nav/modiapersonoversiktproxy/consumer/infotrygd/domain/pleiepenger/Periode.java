package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger;

import java.io.Serializable;
import java.time.LocalDate;

public record Periode(LocalDate fraOgMed, LocalDate tilOgMed) implements Serializable {

    public boolean erGjeldende() {
        LocalDate today = LocalDate.now();
        return ((fraOgMed.isEqual(today) || fraOgMed.isBefore(today)) &&
                (tilOgMed.isEqual(today) || tilOgMed.isAfter(today)));
    }

    public boolean erGjeldendeEllerSenere() {
        return erGjeldende() || fraOgMed.isAfter(LocalDate.now());
    }

}
