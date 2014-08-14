package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Kanal;

public enum SvarKanal implements Kanal {
    TEKST("svarpanel.TEKST.kvittering.bekreftelse"),
    TELEFON("svarpanel.TELEFON.kvittering.bekreftelse"),
    OPPMOTE("svarpanel.OPPMOTE.kvittering.bekreftelse");

    private final String kvitteringKey;

    private SvarKanal(String kvitteringKey) {
        this.kvitteringKey = kvitteringKey;
    }

    @Override
    public String getKvitteringKey() {
        return kvitteringKey;
    }
}
