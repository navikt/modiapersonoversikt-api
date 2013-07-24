package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import org.apache.wicket.ajax.AjaxRequestTarget;

public interface HarMeldingsliste {
    public void valgteMelding(AjaxRequestTarget target, MeldingVM forrigeMelding, MeldingVM valgteMelding, boolean oppdaterScroll);
}
