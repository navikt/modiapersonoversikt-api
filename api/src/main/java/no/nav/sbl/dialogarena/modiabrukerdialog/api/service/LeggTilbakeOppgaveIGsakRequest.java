package no.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

import java.util.Objects;

public class LeggTilbakeOppgaveIGsakRequest {

    private String saksbehandlerValgtEnhet;
    private String oppgaveId;
    private String beskrivelse;
    private Temagruppe nyTemagruppe;

    public LeggTilbakeOppgaveIGsakRequest withSaksbehandlersValgteEnhet(String saksbehandlerValgtEnhet) {
        this.saksbehandlerValgtEnhet = saksbehandlerValgtEnhet;
        return this;
    }

    public LeggTilbakeOppgaveIGsakRequest withOppgaveId(String oppgaveId) {
        this.oppgaveId = oppgaveId;
        return this;
    }

    public LeggTilbakeOppgaveIGsakRequest withBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
        return this;
    }

    public LeggTilbakeOppgaveIGsakRequest withTemagruppe(Temagruppe nyTemagruppe) {
        this.nyTemagruppe = nyTemagruppe;
        return this;
    }

    public String getOppgaveId() {
        return oppgaveId;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public String getSaksbehandlersValgteEnhet() {
        return saksbehandlerValgtEnhet;
    }

    public Temagruppe getNyTemagruppe() {
        return nyTemagruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeggTilbakeOppgaveIGsakRequest)) return false;
        LeggTilbakeOppgaveIGsakRequest that = (LeggTilbakeOppgaveIGsakRequest) o;
        return Objects.equals(saksbehandlerValgtEnhet, that.saksbehandlerValgtEnhet) && Objects.equals(oppgaveId, that.oppgaveId) && Objects.equals(beskrivelse, that.beskrivelse) && nyTemagruppe == that.nyTemagruppe;
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksbehandlerValgtEnhet, oppgaveId, beskrivelse, nyTemagruppe);
    }
}
