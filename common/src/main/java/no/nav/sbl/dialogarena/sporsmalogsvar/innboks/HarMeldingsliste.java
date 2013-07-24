package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Callback interface for paneler som inneholder en PropertyListView<MeldingVM>
 */

public interface HarMeldingsliste {
    /**
     * Callback for å varsle om at ny melding er valgt av brukeren.
     * @param target putt på komponenter/javascript som skal rendres i responsen
     * @param forrigeMelding forrige melding - kan brukes for å slippe å tegne hele listen på nytt
     * @param valgteMelding meldingen brukeren valgte
     * @param oppdaterScroll om valget skal føre til en scroll i meldingslisten
     */
    public void valgteMelding(AjaxRequestTarget target, MeldingVM forrigeMelding, MeldingVM valgteMelding, boolean oppdaterScroll);
}
