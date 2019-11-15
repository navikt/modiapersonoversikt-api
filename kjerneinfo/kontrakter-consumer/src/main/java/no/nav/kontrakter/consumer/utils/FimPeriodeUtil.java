package no.nav.kontrakter.consumer.utils;

import no.nav.kjerneinfo.common.mockutils.DateUtils;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.WSPeriode;

import java.util.Date;

public class FimPeriodeUtil {

    public static boolean periodeInside(no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimPeriode periode, Date cutFrom, Date cutTo) {
        return fromInside(DateUtils.toDate(periode.getFom()), cutFrom, cutTo) || toInside(DateUtils.toDate(periode.getTom()), cutFrom, cutTo);
    }

    public static boolean periodeInside(WSPeriode periode, Date cutFrom, Date cutTo) {
        return fromInside(DateUtils.toDate(periode.getFom()), cutFrom, cutTo) || toInside(DateUtils.toDate(periode.getTom()), cutFrom, cutTo);
    }

    private static boolean fromInside(Date from, Date cutFrom, Date cutTo) {
        return from.compareTo(cutFrom) >= 0 && from.compareTo(cutTo) <= 0;
    }

    private static boolean toInside(Date to, Date cutFrom, Date cutTo) {
        return to.compareTo(cutFrom) >= 0 && to.compareTo(cutTo) <= 0;
    }
}
