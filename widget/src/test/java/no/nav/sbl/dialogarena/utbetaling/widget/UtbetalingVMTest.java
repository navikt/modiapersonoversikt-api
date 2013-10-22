package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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


}
