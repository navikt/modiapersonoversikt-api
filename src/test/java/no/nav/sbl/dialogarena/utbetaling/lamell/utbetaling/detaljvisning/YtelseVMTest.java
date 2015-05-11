package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.YtelseVM.DESC_BELOP;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class YtelseVMTest {

    @Test
    public void sortererYtelserSynkendePaaBelop() {
        List<YtelseVM> ytelser = asList(
            new YtelseVM("ytelse1", 2.0),
            new YtelseVM("ytelse2", -1.0),
            new YtelseVM("ytelse3", 5.0),
            new YtelseVM("ytelse4", 3.0),
            new YtelseVM("ytelse5", -2.0)
        );

        Collections.sort(ytelser, DESC_BELOP);
        assertThat(ytelser.get(0).getYtelse(), is("ytelse3"));
        assertThat(ytelser.get(1).getYtelse(), is("ytelse4"));
        assertThat(ytelser.get(2).getYtelse(), is("ytelse1"));
        assertThat(ytelser.get(3).getYtelse(), is("ytelse2"));
        assertThat(ytelser.get(4).getYtelse(), is("ytelse5"));
    }

    @Test
    public void beggeYtelseneHarBelop() {
        YtelseVM vm1 = new YtelseVM("ytelse 1", 1.0);
        YtelseVM vm2 = new YtelseVM("ytelse 1", 2.0);

        assertThat(DESC_BELOP.compare(vm1, vm2), is(1));
    }

    @Test
    public void vm2HarNullBelop() {
        YtelseVM vm1 = new YtelseVM("ytelse 1", 1.0);
        YtelseVM vm2 = new YtelseVM("ytelse 1", null);

        assertThat(DESC_BELOP.compare(vm1, vm2), is(-1));
    }

    @Test
    public void vm1HarNullBelop() {
        YtelseVM vm1 = new YtelseVM("ytelse 1", null);
        YtelseVM vm2 = new YtelseVM("ytelse 1", 2.0);

        assertThat(DESC_BELOP.compare(vm1, vm2), is(1));
    }

    @Test
    public void satsTypeTomGirIkkeProsent() {
        YtelseVM vm = new YtelseVM("Dagpenger", 10.0, 11.0, 12.0, null);
        assertThat(vm.getSats(), is("12.00"));
    }

    @Test
    public void satsTypeUtenomProsentGirIkkeProsent() {
        YtelseVM vm = new YtelseVM("Dagpenger", 10.0, 11.0, 12.0, "Dag");
        assertThat(vm.getSats(), is("12.00"));
    }

    @Test
    public void satsTypeProsentGirProsentBakSats() {
        YtelseVM vm = new YtelseVM("Dagpenger", 10.0, 11.0, 12.0, "Prosent");
        assertThat(vm.getSats(), is("12.00%"));
    }
}