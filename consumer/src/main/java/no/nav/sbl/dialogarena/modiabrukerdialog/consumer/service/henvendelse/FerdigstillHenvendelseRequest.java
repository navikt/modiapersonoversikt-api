package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

public class FerdigstillHenvendelseRequest {

    public final String fodselsnummer;
    public final String traadId;
    public final String svar;
    public final String henvendelseId;
    public final String navIdent;

    private FerdigstillHenvendelseRequest(FerdigstillHenvendelseRequestBuilder builder) {
        this.fodselsnummer = builder.fodselsnummer;
        this.svar = builder.svar;
        this.traadId = builder.traadId;
        this.henvendelseId = builder.henvendelseId;
        this.navIdent = builder.navIdent;
    }

    public static class FerdigstillHenvendelseRequestBuilder {
        private String svar;
        private String fodselsnummer;
        private String traadId;
        private String henvendelseId;
        private String navIdent;

        public FerdigstillHenvendelseRequestBuilder withFodselsnummer(String fodselsnummer) {
            this.fodselsnummer = fodselsnummer;
            return this;
        }

        public FerdigstillHenvendelseRequestBuilder withTraadId(String traadId) {
            this.traadId = traadId;
            return this;
        }

        public FerdigstillHenvendelseRequestBuilder withHenvendelseId(String henvendelseId) {
            this.henvendelseId = henvendelseId;
            return this;
        }

        public FerdigstillHenvendelseRequestBuilder withSvar(String svar) {
            this.svar = svar;
            return this;
        }

        public FerdigstillHenvendelseRequestBuilder withNavIdent(String navIdent) {
            this.navIdent = navIdent;
            return this;
        }

        public FerdigstillHenvendelseRequest build() {
            return new FerdigstillHenvendelseRequest(this);
        }
    }

}
