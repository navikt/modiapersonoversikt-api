package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.StatiskeLenker;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

public enum HenvendelseType {

    DOKUMENTINNSENDING {
        public String link(Record<Soknad> soknad) {
            return StatiskeLenker.DOKUMENINNSENDING_URL + "/" + soknad.get(Soknad.BEHANDLINGS_ID);
        }

        public String ettersendingLink(Record<Kvittering> kvittering) {
            return StatiskeLenker.NAV_NO_ETTERSENDING;
        }
    },

    SOKNADSINNSENDING {
        public String link(Record<Soknad> soknad) {
            return StatiskeLenker.SOKNADINNSEING_URL + "/soknad/" + soknad.get(Soknad.BEHANDLINGS_ID);
        }

        public String ettersendingLink(Record<Kvittering> kvittering) {
            return StatiskeLenker.SOKNADINNSEING_URL + "/startettersending/" + kvittering.get(Kvittering.BEHANDLINGSKJEDE_ID);
        }
    };

    public abstract String link(Record<Soknad> soknad);

    public abstract String ettersendingLink(Record<Kvittering> kvittering);
}
