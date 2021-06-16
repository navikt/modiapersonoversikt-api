package no.nav.modiapersonoversikt.legacy.sak.providerdomain;

import no.nav.modiapersonoversikt.legacy.sak.utils.StatiskeLenker;

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
