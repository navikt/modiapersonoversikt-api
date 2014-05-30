package no.nav.sbl.dialogarena.sporsmalogsvar.common.model;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;

import java.util.ArrayList;
import java.util.List;

public class MeldingBuffer {
    private static final List<Melding> meldinger = new ArrayList<>();

    public static List<Melding> getMeldinger() {
        return meldinger;
    }

    public static void oppdaterMeldinger(List<Melding> nyeMeldinger) {
        meldinger.clear();
        meldinger.addAll(nyeMeldinger);
    }

}
