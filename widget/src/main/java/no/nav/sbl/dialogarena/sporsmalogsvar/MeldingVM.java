package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.Status;
import no.nav.sbl.dialogarena.sporsmalogsvar.records.Record;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingUtils.NYESTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Meldingstype.INNGAENDE;

public class MeldingVM implements FeedItemVM, Serializable {

    private String id;
    private String avsender, tema;
    private DateTime opprettetDato, lestDato;
    private Status status;

    public MeldingVM(List<Record<Melding>> traad) {
        Record<Melding> nyesteMelding = on(traad).collect(NYESTE_FORST).get(0);
        id = nyesteMelding.get(Melding.id);
        avsender = (traad.size() == 1 ? "Melding" : "Svar") + " fra " +
                (nyesteMelding.get(Melding.type) == INNGAENDE ? "Navn Navnesen" : "NAV");
        tema = nyesteMelding.get(Melding.tema);
        opprettetDato = nyesteMelding.get(Melding.opprettetDato);
        lestDato = nyesteMelding.get(Melding.lestDato);
        status = nyesteMelding.get(Melding.status);
    }


    public String getLestDato() {
        return lestDato == null ? null : DateTimeFormat.forPattern("dd.MM.yyyy").print(lestDato);
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
        return DateTimeFormat.forPattern("dd.MM.yyyy 'kl' HH.mm").print(opprettetDato);
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
