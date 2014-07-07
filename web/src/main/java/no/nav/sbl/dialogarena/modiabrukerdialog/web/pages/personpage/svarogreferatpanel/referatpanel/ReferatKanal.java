package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Kanal;

public enum ReferatKanal implements Kanal {
    TELEFON("referatpanel.TELEFON.kvittering.bekreftelse"),
    OPPMOTE("referatpanel.OPPMOTE.kvittering.bekreftelse");
    private final String kvitteringKey;

    ReferatKanal(String kvitteringKey) {
        this.kvitteringKey = kvitteringKey;
    }

    @Override
    public String getKvitteringKey() {
        return kvitteringKey;
    }
}
