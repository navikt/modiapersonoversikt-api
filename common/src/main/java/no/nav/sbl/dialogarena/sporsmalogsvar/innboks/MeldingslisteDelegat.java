package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * En MeldingslisteDelegat skal orkestrere flere meldingslister gjennom å sørge for at relevate metoder i callback
 * interfacet HarMeldingsliste blir kalt. Delegatet må også oppdatere evt. global tilstand og tjenester.
 */

public interface MeldingslisteDelegat {
    /**
     * Melding valgt. Gi beskjed til andre meldingslister.
     * @param target gir andre paneler mulighet til å rendre komponenter i responsen
     * @param valgtMelding den valgte meldingen
     * @param oppdaterScroll gir beskjed om at andre paneler bør oppdatere scrollposisjonen sin
     */
    public void meldingValgt(AjaxRequestTarget target, MeldingVM valgtMelding, boolean oppdaterScroll);

    /**
     * Paneler kan trenge å vite om en gitt melding er valgt for å vise den.
     * Denne metoden unngår at panelet trenger en referanse til en view model.
     * @param melding som skal sjekkes om er valgt
     * @return om meldingen er valgt
     */
    public IModel<Boolean> erMeldingValgt(MeldingVM melding);
}
