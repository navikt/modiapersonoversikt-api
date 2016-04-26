package no.nav.sbl.dialogarena.utbetaling.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.YtelseVM;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VMUtilsTest {

    @BeforeClass
    public static void setUp() {
        Locale.setDefault(new Locale("no"));
    }

    @Test
    public void mapUnderytelseTilYtelseVMMedAlleVerdier() {
        Underytelse underytelse = new Underytelse()
                .withYtelsesType("ytelsetype")
                .withSatsType("satstype")
                .withSatsBeloep(10.0)
                .withYtelseBeloep(11.0)
                .withSatsAntall(2.0);

        YtelseVM ytelseVM = VMUtils.UNDERYTELSE_TIL_YTELSE_VM.transform(underytelse);
        assertThat(ytelseVM.getYtelse(), is("ytelsetype"));
        assertThat(ytelseVM.getAntall(), is("2,0"));
        assertThat(ytelseVM.getSats(), is("10,00"));
        assertThat(ytelseVM.getBelop(), is("11,00"));
    }

    @Test
    public void mapUnderytelseTilYtelseVMMedProsentSats() {
        Underytelse underytelse = new Underytelse()
                .withYtelsesType("ytelsetype")
                .withSatsType("prosent")
                .withSatsBeloep(10.0)
                .withYtelseBeloep(11.0)
                .withSatsAntall(2.0);

        YtelseVM ytelseVM = VMUtils.UNDERYTELSE_TIL_YTELSE_VM.transform(underytelse);
        assertThat(ytelseVM.getYtelse(), is("ytelsetype"));
        assertThat(ytelseVM.getAntall(), is("2,0"));
        assertThat(ytelseVM.getSats(), is("10,00%"));
        assertThat(ytelseVM.getBelop(), is("11,00"));
    }

    @Test
    public void mapUnderytelseTilYtelseVMUtenSatstype() {
        Underytelse underytelse = new Underytelse()
                .withYtelsesType("ytelsetype")
                .withSatsBeloep(10.0)
                .withYtelseBeloep(11.0)
                .withSatsAntall(2.0);

        YtelseVM ytelseVM = VMUtils.UNDERYTELSE_TIL_YTELSE_VM.transform(underytelse);
        assertThat(ytelseVM.getYtelse(), is("ytelsetype"));
        assertThat(ytelseVM.getAntall(), is("2,0"));
        assertThat(ytelseVM.getSats(), is("10,00"));
        assertThat(ytelseVM.getBelop(), is("11,00"));
    }

}