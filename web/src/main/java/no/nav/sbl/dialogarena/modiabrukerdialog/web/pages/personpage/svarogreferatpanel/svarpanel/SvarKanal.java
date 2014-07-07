package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Kanal;

public enum SvarKanal implements Kanal {
    TEKST,
    TELEFON,
    OPPMOTE;

    @Override
    public String getKvitteringKey() {
        return "svarpanel.kvittering.bekreftelse";
    }
}
