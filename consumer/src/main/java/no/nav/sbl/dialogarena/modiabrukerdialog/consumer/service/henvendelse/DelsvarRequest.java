package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

public class DelsvarRequest {

    public final String fodselsnummer;
    public final String traadId;
    public final String svar;
    public final String henvendelseId;
    public final String navIdent;
    public final String saksbehandlersValgteEnhet;

    private DelsvarRequest(DelsvarRequestBuilder builder) {
        this.fodselsnummer = builder.fodselsnummer;
        this.svar = builder.svar;
        this.traadId = builder.traadId;
        this.henvendelseId = builder.henvendelseId;
        this.navIdent = builder.navIdent;
        this.saksbehandlersValgteEnhet = builder.saksbehandlersValgteEnhet;
    }

    public static class DelsvarRequestBuilder {
        private String svar;
        private String fodselsnummer;
        private String traadId;
        private String henvendelseId;
        private String navIdent;
        private String saksbehandlersValgteEnhet;

        public DelsvarRequestBuilder withFodselsnummer(String fodselsnummer) {
            this.fodselsnummer = fodselsnummer;
            return this;
        }

        public DelsvarRequestBuilder withTraadId(String traadId) {
            this.traadId = traadId;
            return this;
        }

        public DelsvarRequestBuilder withHenvendelseId(String henvendelseId) {
            this.henvendelseId = henvendelseId;
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

        public DelsvarRequest build() {
            return new DelsvarRequest(this);
        }
    }

}
