package no.nav.sbl.dialogarena.sporsmalogsvar;

import java.io.Serializable;
import java.util.List;
import no.nav.modig.modia.model.FeedItemVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import static no.nav.sbl.dialogarena.sporsmalogsvar.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.Status.IKKE_BESVART_INNEN_FRIST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.Status.LEST_AV_BRUKER;

public class MeldingVM implements FeedItemVM, Serializable {

    private String id;
    private String avsender, tema;
    private DateTime opprettetDato, lestDato;
    private Status status;

    public MeldingVM(List<WSHenvendelse> traad) {
        WSHenvendelse nyesteHenvendelse = traad.get(0);
        id = nyesteHenvendelse.getBehandlingsId();
        avsender = (traad.size() == 1 ? "Melding" : "Svar") + " fra " +
                ("SPORSMAL".equals(nyesteHenvendelse.getHenvendelseType()) ? "Navn Navnesen" : "NAV");
        tema = nyesteHenvendelse.getTema();
        opprettetDato = nyesteHenvendelse.getOpprettetDato();
        lestDato = nyesteHenvendelse.getLestDato();
        switch (nyesteHenvendelse.getHenvendelseType()) {
            case "SPORSMAL":
                if (DateTime.now().isAfter(nyesteHenvendelse.getOpprettetDato().plusHours(48))) {
                    status = IKKE_BESVART_INNEN_FRIST;
                } else {
                    status = IKKE_BESVART;
                }
                break;
            case "SVAR":
                if (lestDato != null) {
                    status = LEST_AV_BRUKER;
                } else {
                    status = IKKE_LEST_AV_BRUKER;
                }
                break;
            default:
                throw new RuntimeException("Kjenner ikke til henvendelsetype " + nyesteHenvendelse.getHenvendelseType());
        }
    }


    public String getLestDato() {
        return lestDato == null ? null : DateTimeFormat.forPattern("dd.MM.yyyy").print(lestDato);
    }

    public String getStatusTekst() {
        return status.toString();
    }

    public IModel<String> getStatusKlasse() {
        return new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return "status " + status.toString().toLowerCase().replace(" ", "-");
            }
        };
    }

    public IModel<Boolean> harStatus(final Status status) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return MeldingVM.this.status == status;
            }
        };
    }

    public String getOpprettetDato() {
        return DateTimeFormat.forPattern("dd.MM.yyyy 'kl' HH.mm").print(opprettetDato);
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
        return "Melding";
    }

    @Override
    public String getId() {
        return id;
    }
}
