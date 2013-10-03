package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.melding.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.Status;
import no.nav.sbl.dialogarena.sporsmalogsvar.records.Record;
import org.apache.wicket.Session;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingUtils.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Meldingstype.INNGAENDE;


public class MeldingVM implements Serializable {

    private String id, traadId, tema, avsender, fritekst;

    private Meldingstype type;
    private DateTime opprettetDato, lestDato;
    private boolean lest;
    private int antallMeldingerITraad;
    private Status status;

    public MeldingVM(List<Record<Melding>> tilhorendeTraad, String id) {
        Record<Melding> melding = on(tilhorendeTraad).filter(Melding.id.is(id)).head().get();
        this.id = melding.get(Melding.id);
        traadId = melding.get(Melding.traadId);
        tema = melding.get(Melding.tema);
        type = melding.get(Melding.type);
        avsender = (melding.equals(on(tilhorendeTraad).collect(ELDSTE_FORST).get(0)) ? "Melding" : "Svar") + " fra " +
                (type == INNGAENDE ? "Navn Navnesen" : "NAV");
        fritekst = melding.get(Melding.fritekst);
        opprettetDato = melding.get(Melding.opprettetDato);
        lestDato = melding.get(Melding.lestDato);
        lest = lestDato != null;
        antallMeldingerITraad = tilhorendeTraad.size();
        status = melding.get(Melding.status);
    }

    public String getId() {
        return id;
    }

    public int getAntallMeldingerITraad() {
        return antallMeldingerITraad;
    }

    public String getOpprettetDato() {
        return formatertDato(opprettetDato, "EEEEE dd.MM.yyyy 'kl' HH:mm");
    }

    public String getLestDato() {
        return formatertDato(lestDato, "dd.MM.yyyy 'kl' HH:mm");
    }

    public String formatertDato(final DateTime dato, final String format) {
        return dato == null ? null :
                DateTimeFormat.forPattern(format)
                        .withLocale(Session.get().getLocale())
                        .print(dato);
    }

    public String getTraadId() {
        return traadId;
    }

    public String getTema() {
        return tema;
    }

    public String getAvsender() {
        return avsender;
    }

    public String getFritekst() {
        return fritekst;
    }

    public Meldingstype getType() {
        return type;
    }

    public IModel<Boolean> erLest() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return lest;
            }
        };
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusKlasse() {
        return "indikator-dot " + status.toString().toLowerCase().replace("_", "-");
    }

    public static final Comparator<MeldingVM> NYESTE_OVERST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.opprettetDato.compareTo(o1.opprettetDato);
        }
    };
}
