package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static org.apache.commons.collections15.CollectionUtils.isEqualCollection;
import static org.joda.time.DateTime.now;

public class FilterParametere implements Serializable, Predicate {

    @Override
    public boolean test(Object o) {
        Hovedytelse hovedytelse = (Hovedytelse) o;
        boolean mottakerSkalVises = viseMottaker(hovedytelse.getMottakertype());
        boolean harYtelse = filtrerPaaYtelser(hovedytelse);
        return mottakerSkalVises
                && harYtelse;
    }

    @Override
    public Predicate and(Predicate other) {
        return null;
    }

    @Override
    public Predicate negate() {
        return null;
    }

    @Override
    public Predicate or(Predicate other) {
        return null;
    }

    public enum PeriodeVelger {
        SISTE_30_DAGER,
        INNEVAERENDE_AAR,
        I_FJOR,
        EGENDEFINERT
    }

    public static final String FILTER_ENDRET = "filterParametere.endret";
    public static final String FILTER_FEILET = "filter.validering.feil";
    public static final String PERIODEVALG = "periodevalg.event";
    public static final String YTELSE_FILTER_KLIKKET = "ytelse.filterparametere.klikket";
    public static final String HOVEDYTELSER_ENDRET = "hovedytelser.endret";

    private LocalDate startDato;
    private LocalDate sluttDato;
    private LocalDate visningSluttDato;

    private Map<Mottakertype, Boolean> mottakere;

    private Set<String> alleYtelser;
    private Set<String> onskedeYtelser;
    public PeriodeVelger periodeVelgerValg;

    public FilterParametere(Set<String> hovedYtelser) {
        this.periodeVelgerValg = PeriodeVelger.SISTE_30_DAGER;
        this.startDato = defaultStartDato();
        this.sluttDato = defaultSluttDato();
        this.visningSluttDato = defaultVisningSluttDato();

        this.mottakere = new HashMap<>();
        this.mottakere.put(Mottakertype.ANNEN_MOTTAKER, true);
        this.mottakere.put(Mottakertype.BRUKER, true);

        this.alleYtelser = hovedYtelser;
        this.onskedeYtelser = new HashSet<>(this.alleYtelser);
    }

    public LocalDate getSluttDato() {
        return intervalBasertPaaPeriodevalg(this.periodeVelgerValg).getEnd().toLocalDate();
    }

    public LocalDate getVisningSluttDato() {
        return visningSluttDato;
    }

    public void setSluttDato(LocalDate sluttDato) {
        if (sluttDato != null) {
            this.sluttDato = sluttDato;
        }
    }

    public void setVisningSluttDato(LocalDate visningSluttDato) {
        if (sluttDato != null) {
            this.visningSluttDato = visningSluttDato;
        }
    }

    public LocalDate getStartDato() {
        return intervalBasertPaaPeriodevalg(this.periodeVelgerValg).getStart().toLocalDate();
    }

    public void setStartDato(LocalDate startDato) {
        if (startDato != null) {
            this.startDato = startDato;
        }
    }

    public boolean isAlleYtelserValgt() {
        onskedeYtelser.retainAll(alleYtelser);
        return isEqualCollection(alleYtelser, onskedeYtelser);
    }

    public Set<String> getAlleYtelser() {
        return alleYtelser;
    }

    public void setYtelser(Set<String> hovedYtelser) {
        if (isAlleYtelserValgt()) {
            onskedeYtelser = new HashSet<>(hovedYtelser);
        }
        alleYtelser = hovedYtelser;
    }

    public void toggleMottaker(Mottakertype mottaker) {
        mottakere.put(mottaker, !viseMottaker(mottaker));
    }

    public boolean viseMottaker(Mottakertype mottakerkode) {
        if (mottakere.containsKey(mottakerkode)) {
            return mottakere.get(mottakerkode);
        }
        return false;
    }

    public void toggleAlleYtelser(boolean visAlle) {
        if (visAlle) {
            this.onskedeYtelser.addAll(this.alleYtelser);
        } else {
            this.onskedeYtelser.clear();
        }
    }

    public boolean erYtelseOnsket(String ytelse) {
        return this.onskedeYtelser.contains(ytelse);
    }

    public void velgEnYtelse(String ytelse) {
        this.onskedeYtelser.clear();
        this.onskedeYtelser.add(ytelse);
    }

    public void leggTilOnsketYtelse(String ytelse) {
        this.onskedeYtelser.add(ytelse);
    }

    public void fjernOnsketYtelse(String ytelse) {
        this.onskedeYtelser.remove(ytelse);
    }


    private boolean filtrerPaaYtelser(Hovedytelse utbetaling) {
        return onskedeYtelser.contains(utbetaling.getYtelse());
    }

    protected Interval intervalBasertPaaPeriodevalg(PeriodeVelger valg) {
        DateTime start, end;
        switch(valg) {
            case SISTE_30_DAGER:
                start = now().minusDays(30);
                end = now().plusDays(ANTALL_DAGER_FRAMOVER_I_TID);
                startDato = LocalDate.now().minusDays(30);
                visningSluttDato = LocalDate.now();
                return new Interval(start, end);
            case INNEVAERENDE_AAR:
                start = new DateTime(now().getYear(), 1, 1, 1, 1);
                end = new DateTime(now().getYear(), 12, 31, 23, 59);
                startDato = new LocalDate(now().getYear(), 1, 1);
                visningSluttDato = LocalDate.now();
                return new Interval(start, end);
            case I_FJOR:
                int year = now().getYear() -1;
                start = new DateTime(year, 1, 1, 1, 1);
                end = new DateTime(year, 12, 31, 1, 1);
                startDato = new LocalDate(year, 1, 1);
                visningSluttDato = new LocalDate(year, 12, 31);
                return new Interval(start, end);
            case EGENDEFINERT:
            default:
                return new Interval(startDato.toDateTimeAtStartOfDay(), visningSluttDato.toDateMidnight());
        }
    }

}
