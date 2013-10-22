package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad.Svar;
import org.joda.time.DateTime;
import org.junit.Test;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.Melding.FRITEKST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TraadTest {

    private final DateTime idag0945 = new DateTime(2013, 10, 4, 9, 45);

    @Test
    public void leggTilSetterInnAlleMeldingerSortertPaaTidspunktNyesteForst() {
        Traad traad = new Traad(Tema.HJELPEMIDLER, null);
        traad.leggTil(new Melding("1", INNGAENDE, idag0945.minusDays(1), "Hei!"));
        traad.leggTil(new Melding("1", INNGAENDE, idag0945, "Halla!"));

        assertThat(on(traad.getDialog()).map(FRITEKST), contains("Halla!", "Hei!"));
    }

    @Test
    public void leggTilSetterKunInnMeldingerSomIkkeErITraadFraFoer() {
        Traad traad = new Traad(Tema.HJELPEMIDLER, null);
        traad.leggTil(new Melding("1", INNGAENDE, idag0945.minusDays(1), "Hei!"));
        traad.leggTil(new Melding("1", UTGAENDE, idag0945, "Halla!"));
        traad.leggTil(asList(
                new Melding("1", INNGAENDE, idag0945.plusHours(1), "Hei igjen!"),
                new Melding("1", UTGAENDE, idag0945, "Halla!")));

        assertThat(on(traad.getDialog()).map(FRITEKST), contains("Hei igjen!", "Halla!", "Hei!"));
    }

    @Test
    public void ferdigSvarBlirLagtInnIDialogenOgTraadstatuserOppdateres() {
        Traad traad = new Traad(Tema.HJELPEMIDLER, "svar-42");
        traad.leggTil(new Melding("1", INNGAENDE, idag0945.minusDays(1), "Hei!"));
        assertFalse(traad.erSensitiv);
        traad.erSensitiv = true;

        Svar svar = traad.getSvar();
        svar.fritekst = "Halla!";
        svar.tema = Tema.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT;

        traad.ferdigSvar();

        assertThat(traad.getTema(), is(Tema.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT));
        assertTrue(traad.erSensitiv);
        assertThat(traad.getSisteMelding().fritekst, is("Halla!"));
        assertThat(traad.getSisteMelding().behandlingId, is("svar-42"));


    }
}
