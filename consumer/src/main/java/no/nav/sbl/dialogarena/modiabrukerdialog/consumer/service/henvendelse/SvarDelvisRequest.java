package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

public class SvarDelvisRequest {

    public final String fodselsnummer;
    public final String traadId;
    public final String svar;
    public final String henvendelseId;
    public final String navIdent;
    public final String saksbehandlersValgteEnhet;

    private SvarDelvisRequest(SvarDelvisRequestBuilder builder) {
        this.fodselsnummer = builder.fodselsnummer;
        this.svar = builder.svar;
        this.traadId = builder.traadId;
        this.henvendelseId = builder.henvendelseId;
        this.navIdent = builder.navIdent;
        this.saksbehandlersValgteEnhet = builder.saksbehandlersValgteEnhet;
    }

    public static class SvarDelvisRequestBuilder {
        private String svar;
        private String fodselsnummer;
        private String traadId;
        private String henvendelseId;
        private String navIdent;
        private String saksbehandlersValgteEnhet;

        public SvarDelvisRequestBuilder withFodselsnummer(String fodselsnummer) {
            this.fodselsnummer = fodselsnummer;
            return this;
        }

        public SvarDelvisRequestBuilder withTraadId(String traadId) {
            this.traadId = traadId;
            return this;
        }

        public SvarDelvisRequestBuilder withHenvendelseId(String henvendelseId) {
            this.henvendelseId = henvendelseId;
            return this;
        }

        public SvarDelvisRequestBuilder withSvar(String svar) {
            this.svar = svar;
            return this;
        }

        public SvarDelvisRequestBuilder withNavIdent(String navIdent) {
            this.navIdent = navIdent;
            return this;
        }

        public SvarDelvisRequestBuilder withValgtEnhet(String saksbehandlersValgteEnhet) {
            this.saksbehandlersValgteEnhet = saksbehandlersValgteEnhet;
            return this;
        }

        public SvarDelvisRequest build() {
            return new SvarDelvisRequest(this);
        }
    }

}
