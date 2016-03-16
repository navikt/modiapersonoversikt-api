package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.StatiskeLenker;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

public enum HenvendelseType {

    DOKUMENTINNSENDING {
        public String link(Soknad soknad) {
            return StatiskeLenker.DOKUMENINNSENDING_URL + "/" + soknad.getBehandlingsId();
        }

        public String ettersendingLink(Behandling kvittering) {
            return StatiskeLenker.NAV_NO_ETTERSENDING;
        }
    },

    SOKNADSINNSENDING {
        public String link(Soknad soknad) {
            return StatiskeLenker.SOKNADINNSEING_URL + "/soknad/" + soknad.getBehandlingsId();
        }

        public String ettersendingLink(Behandling kvittering) {
            return StatiskeLenker.SOKNADINNSEING_URL + "/startettersending/" + kvittering.getBehandlingskjedeId();
        }
    };

    public abstract String link(Soknad soknad);

    public abstract String ettersendingLink(Behandling kvittering);
}
