package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

public interface MeldingslisteDelegat {
    public void meldingValgt(AjaxRequestTarget target, MeldingVM valgtMelding, boolean oppdaterScroll);
    public IModel<Boolean> erMeldingValgt(MeldingVM melding);
}
