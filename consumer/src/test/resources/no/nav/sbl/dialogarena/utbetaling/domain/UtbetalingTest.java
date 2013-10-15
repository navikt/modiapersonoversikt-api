package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class UtbetalingTest {

    @Test
    public void extractDatoFromPeriodeString(){
        Utbetaling utbetaling = new UtbetalingBuilder().setPeriode("2011.05.21-2012.02.21").createUtbetaling();
        assertThat(utbetaling.getStartDate() , is(equalTo(new DateTime(2011,5,21,0,0))));
        assertThat(utbetaling.getEndDate() , is(equalTo(new DateTime(2012,2,21,0,0))));
    }


    @Test
    public void extractDatoFromEmptyPeriodeString(){
        Utbetaling utbetaling = new UtbetalingBuilder().setPeriode("").createUtbetaling();
        assertThat(utbetaling.getStartDate() , is(nullValue()));
        assertThat(utbetaling.getEndDate() , is(nullValue()));
    }

}
