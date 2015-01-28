package no.nav.sbl.dialogarena.utbetaling.domain;

public class Konto {
    private String kontonummer;
    private String kontotype;
    private Konto() { }

    public String getKontonummer() {
        return kontonummer;
    }

    public String getKontotype() {
        return kontotype;
    }

    public class KontoBuilder {
        private Konto konto;

        public KontoBuilder() {
            this.konto = new Konto();
        }

        public KontoBuilder withKontonummer(String kontonummer) {
            this.konto.kontonummer = kontonummer;
            return this;
        }

        public KontoBuilder withKontotype(String kontotype) {
            this.konto.kontotype = kontotype;
            return this;
        }

        public Konto build() {
            return this.konto;
        }
    }
}
