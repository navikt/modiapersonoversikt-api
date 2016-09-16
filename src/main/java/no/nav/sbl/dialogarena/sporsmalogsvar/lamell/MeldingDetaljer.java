package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class MeldingDetaljer extends Panel {

    private InnboksVM innboksVM;

    public MeldingDetaljer(String id, InnboksVM innboksVM, MeldingVM meldingVM) {
        super(id);
        setOutputMarkupId(true);
        this.innboksVM = innboksVM;

        WebMarkupContainer meldingDetaljer = new WebMarkupContainer("meldingDetaljer");

        meldingDetaljer.add(new WebMarkupContainer("besvarIndikator").add(visibleIf(blirBesvart(meldingVM.melding.traadId))).setOutputMarkupPlaceholderTag(true));
        meldingDetaljer.add(new Label("visningsDato"));


        meldingDetaljer.add(new StatusIkon("statusIkon",
                blirBesvart(meldingVM.melding.traadId).getObject(),
                meldingVM)
        );

        Label meldingstatus = new Label("meldingstatus", new StringFormatModel("%s - %s",
                new PropertyModel<>(meldingVM, "melding.statusTekst"),
                new PropertyModel<>(meldingVM, "melding.temagruppeNavn")
        ));

        Label dokumentStatus = new Label("meldingstatus", new StringFormatModel("%s",
                new PropertyModel<>(meldingVM, "melding.statusTekst")
        ));

        if (meldingVM.erDokumentMelding) {
            dokumentStatus.setOutputMarkupId(true);
            meldingDetaljer.add(dokumentStatus);
            meldingDetaljer.add(hasCssClassIf("dokument", meldingVM.erDokumentMelding()));
        } else if(meldingVM.erOppgaveMelding) {
            dokumentStatus.setOutputMarkupId(true);
            meldingDetaljer.add(dokumentStatus);
            meldingDetaljer.add(hasCssClassIf("oppgave", meldingVM.erDokumentMelding()));
        } else{
            meldingstatus.setOutputMarkupId(true);
            meldingDetaljer.add(meldingstatus);
            if (meldingVM.traadlengde > 2) {
                meldingDetaljer.add(new AttributeAppender("class", " flere-melding"));
            } else {
                meldingDetaljer.add(new AttributeAppender("class", " en-melding"));
            }
            meldingDetaljer.add(hasCssClassIf("ubesvart", not(meldingVM.erBesvart())));
            meldingDetaljer.add(hasCssClassIf("besvart", meldingVM.erBesvart()));
        }

        meldingDetaljer.add(new Label("fritekst", new PropertyModel<String>(meldingVM, "melding.fritekst")));

        add(meldingDetaljer);

    }

    private AbstractReadOnlyModel<Boolean> blirBesvart(final String traadId) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return traadId.equals(innboksVM.traadBesvares);
            }
        };
    }
}
