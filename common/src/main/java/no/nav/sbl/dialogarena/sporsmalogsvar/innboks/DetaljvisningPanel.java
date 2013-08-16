package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.DokumentinnsendingDetaljPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingstraadPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class DetaljvisningPanel extends Panel implements HarMeldingsliste {

    private Optional<MeldingVM> valgtMelding = Optional.none();

	public DetaljvisningPanel(String id, MeldingslisteDelegat delegat, Optional<MeldingVM> valgtMelding) {
		super(id);
		setOutputMarkupId(true);
        MeldingstraadPanel meldingstraadPanel = new MeldingstraadPanel("traad", delegat);
        DokumentinnsendingDetaljPanel dokumentinnsendingDetaljPanel = new DokumentinnsendingDetaljPanel("dokumentinnsendingDetaljPanel", valgtMelding);
        dokumentinnsendingDetaljPanel.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return DetaljvisningPanel.this.valgtMelding.isSome() && DetaljvisningPanel.this.valgtMelding.get().melding.type == Meldingstype.DOKUMENTINNSENDING;
            }
        }));
        meldingstraadPanel.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return DetaljvisningPanel.this.valgtMelding.isSome() && DetaljvisningPanel.this.valgtMelding.get().melding.type != Meldingstype.DOKUMENTINNSENDING;
            }
        }));
        Label ingenValgt = new Label("ingen-valgt", new Model<String>("Ingen melding valgt"));
        ingenValgt.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !DetaljvisningPanel.this.valgtMelding.isSome();
            }
        }));
		add(meldingstraadPanel, dokumentinnsendingDetaljPanel, ingenValgt);
	}

	@Override
	public void valgteMelding(AjaxRequestTarget target, MeldingVM forrigeMelding, MeldingVM valgteMelding, boolean oppdaterScroll) {
		this.valgtMelding = Optional.optional(valgteMelding);
		target.add(this);
	}

}
