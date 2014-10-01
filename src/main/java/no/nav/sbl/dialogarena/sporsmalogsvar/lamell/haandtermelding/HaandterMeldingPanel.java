package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.pdf.PdfMerger;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave.NyOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.time.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static org.apache.wicket.event.Broadcast.EXACT;

public class HaandterMeldingPanel extends Panel {

    static final String PANEL_TOGGLET = "sos.haandtermelding.panelTogglet";
    static final String PANEL_LUKKET = "sos.haandtermelding.panelLukket";

    public HaandterMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id, new PropertyModel(innboksVM, "valgtTraad"));

        IModel<Boolean> erKontorsperret = new PropertyModel<>(getDefaultModel(), "erKontorsperret()");
        IModel<Boolean> erFeilsendt = new PropertyModel<>(getDefaultModel(), "erFeilsendt()");
        IModel<Boolean> nyesteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "nyesteMelding.journalfort");
        IModel<Boolean> eldsteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "eldsteMelding.journalfort");
        IModel<Boolean> erBehandlet = new PropertyModel<>(getDefaultModel(), "erBehandlet()");
        IModel<Boolean> bleInitiertAvBruker = new PropertyModel<>(getDefaultModel(), "bleInitiertAvBruker()");

        add(
            new AjaxLink<InnboksVM>("besvar") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    send(getPage(), EXACT, new NamedEventPayload(SVAR_PAA_MELDING, innboksVM.getValgtTraad().getEldsteMelding().melding.id));
                }
            }.add(enabledIf(bleInitiertAvBruker))
        );

        JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforPanel", innboksVM);
        add(journalforingsPanel);
        add(new MeldingValgPanel("journalforingValg", both(not(erKontorsperret)).and(not(erFeilsendt)).and(not(nyesteMeldingErJournalfort)), journalforingsPanel));

        NyOppgavePanel nyOppgavePanel = new NyOppgavePanel("nyoppgavePanel", innboksVM);
        add(nyOppgavePanel);
        add(new MeldingValgPanel("nyoppgaveValg", erBehandlet, nyOppgavePanel));

        MerkePanel merkePanel = new MerkePanel("merkePanel", innboksVM);
        add(merkePanel);
        add(new MeldingValgPanel("merkeValg", both(not(eldsteMeldingErJournalfort)).and(erBehandlet), merkePanel));

        DownloadLink downloadLink = new DownloadLink("print", new LoadableDetachableModel<File>() {
            @Override
            protected File load() {
                File henvendelser;
                try {
                    byte[] finalPdf = settSammenTilEnPdf();
                    ByteArrayInputStream data = new ByteArrayInputStream(finalPdf);
                    henvendelser = File.createTempFile("henvendelser", null);
                    Files.writeTo(henvendelser, data);
                    data.close();
                } catch (IOException e) {
                    throw new RuntimeException("Feil ved generering av PDF", e);
                }
                return henvendelser;
            }


            private byte[] settSammenTilEnPdf() {
                List<byte[]> pdfDokumenter = new ArrayList<>();
                List<MeldingVM> meldinger = innboksVM.getValgtTraad().getMeldinger();
                for (MeldingVM meldingVM : meldinger) {
                    pdfDokumenter.add(PdfUtils.genererPdfForPrint(meldingVM.melding));
                }
                return new PdfMerger().transform(pdfDokumenter);
            }

        }, "meldinger.pdf");
        downloadLink.setCacheDuration(Duration.NONE);
        downloadLink.setDeleteAfterDownload(true);
        add(downloadLink);

    }
}

