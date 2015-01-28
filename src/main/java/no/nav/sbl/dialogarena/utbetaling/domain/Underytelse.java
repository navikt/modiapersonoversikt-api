package no.nav.sbl.dialogarena.utbetaling.domain;

public class Underytelse {
    private String ytelsesType;
    private Double satsBeloep;
    private String satsType;
    private Integer satsAntall;
    private Double ytelseBeloep;
    private Underytelse() {}

    public class UnderytelseBuilder {
        private Underytelse underytelse;

        public UnderytelseBuilder() {
            this.underytelse = new Underytelse();
        }

        public UnderytelseBuilder withYtelsesType(String ytelsesKomponentType) {
            this.underytelse.ytelsesType = ytelsesKomponentType;
            return this;
        }

        public UnderytelseBuilder withSatsBeloep(Double satsBeloep) {
            this.underytelse.satsBeloep = satsBeloep;
            return this;
        }

        public UnderytelseBuilder withSatsType(String satsType) {
            this.underytelse.satsType = satsType;
            return this;
        }

        public UnderytelseBuilder withSatsAntall(Integer satsAntall) {
            this.underytelse.satsAntall = satsAntall;
            return this;
        }

        public UnderytelseBuilder withYtelseBeloep(Double ytelseBeloep) {
            this.underytelse.ytelseBeloep = ytelseBeloep;
            return this;
        }

        public Underytelse build() {
            return this.underytelse;
        }
    }
}
