package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UtbetalingVMTest {

    @Test
    public void canSerializeObject() {
        Utbetaling utbetaling1 = new UtbetalingBuilder().createUtbetaling();
        SerializationUtils.serialize(utbetaling1);

        UtbetalingVM utbetalingVM = new UtbetalingVM(utbetaling1);
        SerializationUtils.serialize(utbetalingVM);
    }

    @Test
    public void canSerializeListOfObject() {
        Utbetaling utbetaling = new UtbetalingBuilder().createUtbetaling();
        List<UtbetalingVM> utbetalingVMer = new ArrayList<>();
        UtbetalingVM utbetalingVM = new UtbetalingVM(utbetaling);
        utbetalingVMer.add(utbetalingVM);
        utbetalingVMer.add(utbetalingVM);
        utbetalingVMer.add(utbetalingVM);
        utbetalingVMer.add(utbetalingVM);
        SerializationUtils.serialize((Serializable) utbetalingVMer);
    }

    @Test
    public void belopFormateres_medGruppering_medKomma_medToDesimaler() throws Exception {
        double belop = 67856565.6;
        Utbetaling utbetaling = new UtbetalingBuilder().setNettoBelop(belop).createUtbetaling();
        UtbetalingVM vm = new UtbetalingVM(utbetaling);

        String belop1 = vm.getBelop();
        System.out.println("belop = " + belop1);

        String[] splittPaaKomma = belop1.split(",");
        assertThat(splittPaaKomma.length, is(equalTo(2)));
        assertThat(splittPaaKomma[1], is("60"));
    }
}
