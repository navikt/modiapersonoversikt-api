package no.nav.modiapersonoversikt.consumer.kontrakter.domain.ytelse;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DagpengeytelseTest {

    public static final int DAGER_IGJEN = 20;
    public static final int UKER_IGJEN = 10;
    public static final int DAGER_IGJEN_PERMITTERING = 20;
    public static final int UKER_IGJEN_PERMITTERING = 10;
    public static final String VEDTAKSTATUS = "vedtakstatus";
    private static final LocalDate FOM = new LocalDate(System.currentTimeMillis() - 891569941);
    private static final LocalDate TOM = new LocalDate(System.currentTimeMillis() + 891555641);

    @Test
    public void testBean() {
        Dagpengeytelse ytelse = new Dagpengeytelse();
        ytelse.setAntallDagerIgjen(DAGER_IGJEN);
        ytelse.setAntallUkerIgjen(UKER_IGJEN);
        ytelse.setAntallDagerIgjenPermittering(DAGER_IGJEN_PERMITTERING);
        ytelse.setAntallUkerIgjenPermittering(UKER_IGJEN_PERMITTERING);
        ytelse.setFom(FOM);
        ytelse.setTom(TOM);
        List<Vedtak> vedtakList = new ArrayList<>();
        Vedtak vedtak = new Vedtak();
        vedtak.setVedtakstatus(VEDTAKSTATUS);
        vedtakList.add(vedtak);
        ytelse.setVedtak(vedtakList);
        assertEquals(Integer.valueOf(DAGER_IGJEN), ytelse.getAntallDagerIgjen());
        assertEquals(Integer.valueOf(UKER_IGJEN), ytelse.getAntallUkerIgjen());
        assertEquals(VEDTAKSTATUS, ytelse.getVedtak().get(0).getVedtakstatus());
        assertEquals(FOM, ytelse.getFom());
        assertEquals(TOM, ytelse.getTom());
        assertEquals(Integer.valueOf(DAGER_IGJEN_PERMITTERING), ytelse.getAntallDagerIgjenPermittering());
        assertEquals(Integer.valueOf(UKER_IGJEN_PERMITTERING), ytelse.getAntallUkerIgjenPermittering());
    }
}
