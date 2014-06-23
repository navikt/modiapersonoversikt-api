package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TraadVMTest {

    @Test
    public void skalFinneRiktigTraadlengde() {
        TraadVM traadVM = new TraadVM(new ArrayList<>(Arrays.asList(new MeldingVM(
                new ArrayList<>(Arrays.asList(
                        new Melding("id1", Meldingstype.SAMTALEREFERAT, DateTime.now()))),
                new Melding("id2", Meldingstype.SAMTALEREFERAT, DateTime.now())))));
        int traadLengde = traadVM.getTraadLengde();
        assertThat(traadLengde, is(1));
    }

}
