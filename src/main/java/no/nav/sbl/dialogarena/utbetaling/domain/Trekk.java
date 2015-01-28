package no.nav.sbl.dialogarena.utbetaling.domain;

public class Trekk {

    private String trekksType;
    private Double trekkBeloep;
    private String kreditor;
    private Trekk() { }

    public class TrekkBuilder {
        private Trekk trekk;

        public TrekkBuilder() {
            this.trekk = new Trekk();
        }

        public TrekkBuilder withTrekksType(String trekksType) {
            this.trekk.trekksType = trekksType;
            return this;
        }

        public TrekkBuilder withTrekkBeloep(Double trekkBeloep) {
            this.trekk.trekkBeloep = trekkBeloep;
            return this;
        }

        public TrekkBuilder withKreditor(String kreditor) {
            this.trekk.kreditor = kreditor;
            return this;
        }

        public Trekk build() {
            return this.trekk;
        }
    }
}
