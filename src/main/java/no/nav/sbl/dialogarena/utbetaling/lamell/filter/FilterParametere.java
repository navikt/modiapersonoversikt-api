package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Predicate;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.Mottaktertype;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;


public class FilterParametere implements Serializable, Predicate<Utbetaling> {

    public static final String FILTER_ENDRET = "filterParametere.endret";
    public static final String HOVEDYTELSER_ENDRET = "hovedytelser.endret";

    private LocalDate startDato;
    private LocalDate sluttDato;

    private Map<Mottaktertype, Boolean> mottakere;

    private boolean alleYtelserValgt;

    private Set<String> alleYtelser;
    private Set<String> onskedeYtelser;

    public FilterParametere(Set<String> hovedYtelser) {
        this.startDato = defaultStartDato();
        this.sluttDato = defaultSluttDato();

        this.mottakere = new HashMap<>();
        this.mottakere.put(Mottaktertype.ANNEN_MOTTAKER, true);
        this.mottakere.put(Mottaktertype.BRUKER, true);

        this.alleYtelserValgt = true;
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
        return alleYtelserValgt;
    }

    public Set<String> getAlleYtelser() {
        return alleYtelser;
    }

    public void setYtelser(Set<String> hovedYtelser) {
        if (alleYtelserValgt) {
            onskedeYtelser.addAll(hovedYtelser);
        }
        alleYtelser = hovedYtelser;
    }

    public void toggleMottaker(Mottaktertype mottaker) {
        mottakere.put(mottaker, !viseMottaker(mottaker));
    }

    public boolean viseMottaker(Mottaktertype mottakerkode) {
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
    public boolean evaluate(Utbetaling utbetaling) {
        boolean innenforDatoer = filtrerPaaDatoer(utbetaling.getUtbetalingsdato().toLocalDate());
        boolean mottakerSkalVises = viseMottaker(utbetaling.getMottaktertype());
        boolean harYtelse = filtrerPaaYtelser(utbetaling);
        return innenforDatoer
                && mottakerSkalVises
                && harYtelse;
    }

    private boolean filtrerPaaDatoer(LocalDate utbetalingsDato) {
        return utbetalingsDato.isAfter(startDato.minusDays(1)) && utbetalingsDato.isBefore(sluttDato.plusDays(1));
    }

    private boolean filtrerPaaYtelser(Utbetaling utbetaling) {
        return onskedeYtelser.contains(utbetaling.getHovedytelse());
    }

}
