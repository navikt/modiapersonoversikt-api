package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.meldinger.Etikett;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.meldinger.MeldingEtiketter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

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
                innboksVM.getTraader().get(meldingVM.melding.traadId),
                blirBesvart(meldingVM.melding.traadId).getObject())
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
        meldingDetaljer.add(new MeldingEtiketter("melding-etiketter", "etikett", lagEtiketter(meldingVM, innboksVM)));

        add(meldingDetaljer);
    }

    private List<Etikett> lagEtiketter(MeldingVM meldingVM, InnboksVM innboksVM) {
        TraadVM traadVM = getTraad(meldingVM, innboksVM);

        List<Etikett> etiketter = new ArrayList<>();
        if (traadVM.harDelsvar()) {
            etiketter.add(new Etikett("Delvis besvart", "delsvar"));
        }
        if (erTildelt(traadVM.getEldsteMelding().getTraadId())) {
            etiketter.add(new Etikett("Tildelt meg", "tildelt"));
        }
        return etiketter;
    }

    private TraadVM getTraad(MeldingVM meldingVM, InnboksVM innboksVM) {
        return innboksVM.getTraader().get(meldingVM.getTraadId());
    }

    private boolean erTildelt(final String traadId) {
        return innboksVM.tildelteOppgaver.stream()
                .anyMatch(oppgave -> traadId.equals(oppgave.henvendelseId));
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
