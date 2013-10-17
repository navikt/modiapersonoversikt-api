package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import org.joda.time.DateTime;
import org.junit.Test;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.Melding.FRITEKST;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class TraadTest {

    private final DateTime idag0945 = new DateTime(2013, 10, 4, 9, 45);

    @Test
    public void mergeSetterInnAlleMeldingerSortertPaaTidspunktNyesteForst() {
        Traad traad = new Traad();
        traad.leggTil(new Melding("1", INNGAENDE, idag0945.minusDays(1), "Hei!"));

        Traad oppdatertTraad = new Traad();
        oppdatertTraad.leggTil(new Melding("1", INNGAENDE, idag0945, "Halla!"));

        traad.merge(oppdatertTraad);
        assertThat(on(traad.getDialog()).map(FRITEKST), contains("Halla!", "Hei!"));
    }

    @Test
    public void mergeSetterKunInnMeldingerSomIkkeErITraadFraFoer() {
        Traad traad = new Traad();
        traad.leggTil(new Melding("1", INNGAENDE, idag0945.minusDays(1), "Hei!"));
        traad.leggTil(new Melding("1", UTGAENDE, idag0945, "Halla!"));

        Traad oppdatertTraad = new Traad();
        oppdatertTraad.leggTil(new Melding("1", UTGAENDE, idag0945, "Halla!"));
        oppdatertTraad.leggTil(new Melding("1", INNGAENDE, idag0945.plusHours(1), "Hei igjen!"));

        traad.merge(oppdatertTraad);
        assertThat(on(traad.getDialog()).map(FRITEKST), contains("Hei igjen!", "Halla!", "Hei!"));
    }
}
