package no.nav.sbl.dialogarena.utbetaling.util;

import no.nav.sbl.dialogarena.common.records.Record;
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
        Record<Underytelse> underytelse = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "ytelsetype")
                .with(Underytelse.satsType, "satstype")
                .with(Underytelse.satsBeloep, 10.0)
                .with(Underytelse.ytelseBeloep, 11.0)
                .with(Underytelse.satsAntall, 2.0);

        YtelseVM ytelseVM = VMUtils.UNDERYTELSE_TIL_YTELSE_VM.transform(underytelse);
        assertThat(ytelseVM.getYtelse(), is("ytelsetype"));
        assertThat(ytelseVM.getAntall(), is("2,0"));
        assertThat(ytelseVM.getSats(), is("10,00"));
        assertThat(ytelseVM.getBelop(), is("11,00"));
    }

    @Test
    public void mapUnderytelseTilYtelseVMMedProsentSats() {
        Record<Underytelse> underytelse = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "ytelsetype")
                .with(Underytelse.satsType, "prosent")
                .with(Underytelse.satsBeloep, 10.0)
                .with(Underytelse.ytelseBeloep, 11.0)
                .with(Underytelse.satsAntall, 2.0);

        YtelseVM ytelseVM = VMUtils.UNDERYTELSE_TIL_YTELSE_VM.transform(underytelse);
        assertThat(ytelseVM.getYtelse(), is("ytelsetype"));
        assertThat(ytelseVM.getAntall(), is("2,0"));
        assertThat(ytelseVM.getSats(), is("10,00%"));
        assertThat(ytelseVM.getBelop(), is("11,00"));
    }

    @Test
    public void mapUnderytelseTilYtelseVMUtenSatstype() {
        Record<Underytelse> underytelse = new Record<Underytelse>()
                .with(Underytelse.ytelsesType, "ytelsetype")
                .with(Underytelse.satsBeloep, 10.0)
                .with(Underytelse.ytelseBeloep, 11.0)
                .with(Underytelse.satsAntall, 2.0);

        YtelseVM ytelseVM = VMUtils.UNDERYTELSE_TIL_YTELSE_VM.transform(underytelse);
        assertThat(ytelseVM.getYtelse(), is("ytelsetype"));
        assertThat(ytelseVM.getAntall(), is("2,0"));
        assertThat(ytelseVM.getSats(), is("10,00"));
        assertThat(ytelseVM.getBelop(), is("11,00"));
    }

}