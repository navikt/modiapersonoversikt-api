package no.nav.modiapersonoversikt.service.henvendelse;

public class DelsvarRequest {

    public final String fodselsnummer;
    public final String traadId;
    public final String svar;
    public final String behandlingsId;
    public final String navIdent;
    public final String saksbehandlersValgteEnhet;
    public final String oppgaveId;
    public final String temagruppe;

    private DelsvarRequest(DelsvarRequestBuilder builder) {
        this.fodselsnummer = builder.fodselsnummer;
        this.svar = builder.svar;
        this.traadId = builder.traadId;
        this.behandlingsId = builder.behandlingsId;
        this.navIdent = builder.navIdent;
        this.saksbehandlersValgteEnhet = builder.saksbehandlersValgteEnhet;
        this.oppgaveId = builder.oppgaveId;
        this.temagruppe = builder.temagruppe;
    }

    public static class DelsvarRequestBuilder {
        private String svar;
        private String fodselsnummer;
        private String traadId;
        private String behandlingsId;
        private String navIdent;
        private String saksbehandlersValgteEnhet;
        private String oppgaveId;
        private String temagruppe;

        public DelsvarRequestBuilder withFodselsnummer(String fodselsnummer) {
            this.fodselsnummer = fodselsnummer;
            return this;
        }

        public DelsvarRequestBuilder withTraadId(String traadId) {
            this.traadId = traadId;
            return this;
        }

        public DelsvarRequestBuilder withBehandlingsId(String behandlingsId) {
            this.behandlingsId = behandlingsId;
            return this;
        }

        public DelsvarRequestBuilder withSvar(String svar) {
            this.svar = svar;
            return this;
        }

        public DelsvarRequestBuilder withNavIdent(String navIdent) {
            this.navIdent = navIdent;
            return this;
        }

        public DelsvarRequestBuilder withValgtEnhet(String saksbehandlersValgteEnhet) {
            this.saksbehandlersValgteEnhet = saksbehandlersValgteEnhet;
            return this;
        }

        public DelsvarRequestBuilder withOppgaveId(String oppgaveId) {
            this.oppgaveId = oppgaveId;
            return this;
        }

        public DelsvarRequestBuilder withTemagruppe(String temagruppe) {
            this.temagruppe = temagruppe;
            return this;
        }

        public DelsvarRequest build() {
            return new DelsvarRequest(this);
        }
    }

}
