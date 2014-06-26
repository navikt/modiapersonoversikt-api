package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

/**
 * For å teste DialogPanel uten å være avhengig av en spesifikk underklasse
 */
class TestDialogPanel extends DialogPanel {
    public TestDialogPanel(String id, String fnr) {
        super(id, fnr);
    }

    @Override
    protected void sendHenvendelse(DialogVM dialogVM, String fnr) {

    }

    static enum TestKanal implements Kanal {
        TEST;

        @Override
        public String getKvitteringKey() {
            return "svarpanel.kvittering.bekreftelse";
        }
    }
}
