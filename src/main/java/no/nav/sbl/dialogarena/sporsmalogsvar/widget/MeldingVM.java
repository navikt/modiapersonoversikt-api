package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.MeldingRecord;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.NYESTE_FORST;

public class MeldingVM implements FeedItemVM, Serializable {

    private String id;
    private String avsender, tema;
    private DateTime opprettetDato, lestDato;
    private Status status;

    public MeldingVM(Iterable<Record<MeldingRecord>> traad) {
        List<Record<MeldingRecord>> sortertTraad = on(traad).collect(NYESTE_FORST);
        Record<MeldingRecord> nyesteMelding = sortertTraad.get(0);
        id = nyesteMelding.get(MeldingRecord.traadId);
        avsender = (sortertTraad.size() == 1 ? "Melding" : "Svar") + " fra " +
                (nyesteMelding.get(MeldingRecord.type) == INNGAENDE ? "Bruker" : "NAV");
        tema = nyesteMelding.get(MeldingRecord.tema);
        opprettetDato = nyesteMelding.get(MeldingRecord.opprettetDato);
        lestDato = nyesteMelding.get(MeldingRecord.lestDato);
        status = nyesteMelding.get(MeldingRecord.status);
    }


    public String getLestDato() {
        return Datoformat.ultrakort(lestDato);
    }

    public IModel<Boolean> harStatus(final Status status) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return MeldingVM.this.status == status;
            }
        };
    }

    public String getStatusKlasse() {
        return "status " + status.toString().toLowerCase().replace("_", "-");
    }

    public String getOpprettetDatoAsString() {
        return Datoformat.langMedTid(opprettetDato);
    }

    public DateTime getOpprettetDato() {
        return opprettetDato;
    }

    public void setOpprettetDato(DateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
    }

    public String getAvsender() {
        return avsender;
    }

    public void setAvsender(String avsender) {
        this.avsender = avsender;
    }

    public String getTema() {
        return tema;
    }


    public void setTema(String tema) {
        this.tema = tema;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getType() {
        return "meldinger";
    }

    @Override
    public String getId() {
        return id;
    }
}
