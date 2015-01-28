package no.nav.sbl.dialogarena.utbetaling;

public class Aktoer {

    private String aktoerId;
    private String navn;

    private Aktoer() { }

    public static class AktoerBuilder {
        private Aktoer aktoer;

        public AktoerBuilder() {
            this.aktoer = new Aktoer();
        }

        public AktoerBuilder withAktoerId(String aktoerId) {
            this.aktoer.aktoerId = aktoerId;
            return this;
        }

        public AktoerBuilder withNavn(String navn) {
            this.aktoer.navn = navn;
            return this;
        }

        public Aktoer build() {
            return this.aktoer;
        }
    }
}
