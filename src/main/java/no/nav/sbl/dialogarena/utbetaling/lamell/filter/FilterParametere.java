package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import org.apache.commons.collections15.Predicate;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultStartDato;
import static org.apache.commons.collections15.CollectionUtils.isEqualCollection;

public class FilterParametere implements Serializable, Predicate<Record<Hovedytelse>> {

    public enum PeriodeVelger {
        SISTE_3_MND,
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

    private Map<Mottakertype, Boolean> mottakere;

    private Set<String> alleYtelser;
    private Set<String> onskedeYtelser;
    public PeriodeVelger periodeVelgerValg;

    public FilterParametere(Set<String> hovedYtelser) {
        this.periodeVelgerValg = PeriodeVelger.SISTE_3_MND;
        this.startDato = defaultStartDato();
        this.sluttDato = defaultSluttDato();

        this.mottakere = new HashMap<>();
        this.mottakere.put(Mottakertype.ANNEN_MOTTAKER, true);
        this.mottakere.put(Mottakertype.BRUKER, true);

        this.alleYtelser = hovedYtelser;
        this.onskedeYtelser = new HashSet<>(this.alleYtelser);
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }

    public void setSluttDato(LocalDate sluttDato) {
        if (sluttDato != null) {
            this.sluttDato = sluttDato;
        }
    }

    public LocalDate getStartDato() {
        return startDato;
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

    @Override
    public boolean evaluate(Record<Hovedytelse> hovedytelse) {
        boolean mottakerSkalVises = viseMottaker(hovedytelse.get(Hovedytelse.mottakertype));
        boolean harYtelse = filtrerPaaYtelser(hovedytelse);
        return mottakerSkalVises
                && harYtelse;
    }

    private boolean filtrerPaaYtelser(Record<Hovedytelse> utbetaling) {
        return onskedeYtelser.contains(utbetaling.get(Hovedytelse.ytelse));
    }
}
