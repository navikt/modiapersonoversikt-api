package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.common.melding.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.common.melding.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.common.melding.Status;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.common.records.Record;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.common.utils.MeldingUtils.ELDSTE_FORST;


public class MeldingVM implements Serializable {

    private String id;
    private String traadId;
    private String tema;
    private String avsender;
    private String fritekst;

    private String journalfortTema;
    private String journalfortSakdId;
    private Meldingstype type;
    public final DateTime opprettetDato, lestDato, journalfortDato;
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
                (type == INNGAENDE ? "Bruker" : "NAV");
        fritekst = melding.get(Melding.fritekst);
        opprettetDato = melding.get(Melding.opprettetDato);
        lestDato = melding.get(Melding.lestDato);
        lest = lestDato != null;
        antallMeldingerITraad = tilhorendeTraad.size();
        status = melding.get(Melding.status);
        journalfortDato = melding.get(Melding.journalfortDato);
        journalfortSakdId = melding.get(Melding.journalfortSaksid);
        journalfortTema = melding.get(Melding.journalfortTema);
    }

    public String getId() {
        return id;
    }

    public int getAntallMeldingerITraad() {
        return antallMeldingerITraad;
    }

    public String getOpprettetDato() {
        return Datoformat.langMedTid(opprettetDato);
    }

    public String getLestDato() {
        return Datoformat.kortMedTid(lestDato);
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

    public String getJournalfortTema() {
        return journalfortTema;
    }

    public void setJournalfortTema(String journalfortTema) {
        this.journalfortTema = journalfortTema;
    }

    public String getJournalfortSakdId() {
        return journalfortSakdId;
    }

    public void setJournalfortSakdId(String journalfortSakdId) {
        this.journalfortSakdId = journalfortSakdId;
    }

    public DateTime getJournalfortDato() {
        return journalfortDato;
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
