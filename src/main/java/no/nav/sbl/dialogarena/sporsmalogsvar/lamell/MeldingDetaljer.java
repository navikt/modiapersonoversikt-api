package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.*;

public class MeldingDetaljer extends Panel {

    private InnboksVM innboksVM;

    public MeldingDetaljer(String id, InnboksVM innboksVM, MeldingVM meldingVM) {
        super(id);
        setOutputMarkupId(true);
        this.innboksVM = innboksVM;
        String traadId = meldingVM.melding.traadId;

        WebMarkupContainer meldingDetaljer = new WebMarkupContainer("meldingDetaljer");

        WebMarkupContainer indikator = new WebMarkupContainer("besvarIndikator");
        indikator.add(visibleIf(either(blirBesvart(traadId)).or(erTildelt(traadId))))
                .add(hasCssClassIf("besvarIndikator", blirBesvart(traadId)))
                .add(hasCssClassIf("tildeltIndikator", both(erTildelt(traadId)).and(not(blirBesvart(traadId)))))
                .setOutputMarkupPlaceholderTag(true);
        indikator.add(new Label("tildelt",
                new StringResourceModel("melding.erTildelt", this, null))
                .add(visibleIf(both(not(blirBesvart(traadId))).and(erTildelt(traadId)))));
        meldingDetaljer.add(indikator);

        meldingDetaljer.add(new Label("visningsDato"));


        meldingDetaljer.add(new StatusIkon("statusIkon",
                innboksVM.getTraader().get(traadId),
                blirBesvart(traadId).getObject())
        );

        Label meldingstatus = new Label("meldingstatus", new StringFormatModel("%s – %s",
                new PropertyModel<>(meldingVM, "melding.statusTekst"),
                new PropertyModel<>(meldingVM, "melding.temagruppeNavn")
        ));

        Label dokumentStatus = new Label("meldingstatus", new StringFormatModel("%s",
                new PropertyModel<>(meldingVM, "melding.statusTekst")
        ));

        if (meldingVM.erDokumentMelding || meldingVM.erOppgaveMelding) {
            dokumentStatus.setOutputMarkupId(true);
            meldingDetaljer.add(dokumentStatus);
        } else {
            meldingstatus.setOutputMarkupId(true);
            meldingDetaljer.add(meldingstatus);
        }

        meldingDetaljer.add(new Label("fritekst", new PropertyModel<String>(meldingVM, "melding.fritekst")));

        add(meldingDetaljer);

    }

    private AbstractReadOnlyModel<Boolean> erTildelt(final String traadId) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.tildelteOppgaver.stream()
                        .anyMatch(oppgave -> traadId.equals(oppgave.henvendelseId));
            }
        };
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
