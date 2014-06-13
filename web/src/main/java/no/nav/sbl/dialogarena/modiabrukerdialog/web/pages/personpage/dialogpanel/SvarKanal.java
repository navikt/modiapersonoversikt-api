package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

public enum SvarKanal implements Kanal {
    TEKST,
    TELEFON,
    OPPMOTE;

    @Override
    public String getKvitteringKey() {
        return "svarpanel.kvittering.bekreftelse";
    }
}
