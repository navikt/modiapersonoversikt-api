package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.wicket.component.modal.ModalAdvarselPanel;
import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.util.ResourceStreamAjaxBehaviour;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Dokument;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.util.List;

import static java.lang.String.format;
import static no.nav.modig.modia.widget.utils.WidgetDateFormatter.date;
import static no.nav.modig.modia.widget.utils.WidgetDateFormatter.dateTime;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static org.apache.wicket.model.Model.of;

public class KvitteringsPanel extends Panel {

    @Inject
    private BulletproofCmsService cms;

    @Inject
    private BulletProofKodeverkService kodeverk;

    private Logger logger = LoggerFactory.getLogger(KvitteringsPanel.class);
    private ModigModalWindow modalWindow;

    public KvitteringsPanel(String id, String tittel, Model<Kvittering> kvitteringsModel, String fnr) {
        super(id, kvitteringsModel);
        Kvittering kvittering = kvitteringsModel.getObject();

        int antallInnsendteVedlegg = kvittering.innsendteDokumenter.size();
        int totalAntallVedlegg = antallInnsendteVedlegg + kvittering.manglendeDokumenter.size();

        String dato = dateTime((kvittering.behandlingDato));
        String sendtAvString = cms.hentTekst("kvittering.sendt.av");

        String sendtInnTekst;
        if (totalAntallVedlegg > 0) {
            sendtInnTekst = format(cms.hentTekst("kvittering.vedlegg.sendtInn.antall"), antallInnsendteVedlegg, totalAntallVedlegg, dato);
        } else {
            sendtInnTekst = dato;
        }

        add(
                new Label("hendelse-tittel", tittel),
                new Label("dato-topp", date(kvittering.behandlingDato)),
                new Label("sendtAv", format(sendtAvString, fnr)),
                new Label("kvitteringsinfo-bottom", cms.hentTekst("kvittering.info.bottom")).setEscapeModelStrings(false),
                new Label("sendt-inn", sendtInnTekst),
                new Label("skjul-kollapsbar", cms.hentTekst("kvittering.skjul")),
                new Label("vis-kollapsbar", cms.hentTekst("kvittering.vis"))
        );

        leggTilInnsendteVedlegg(kvittering);
        leggTilManglendeVedlegg(kvittering);
        leggTilBehandlingsTidInfo(kvittering);
        leggTilVedleggFeiletPoput();
    }

    private void leggTilBehandlingsTidInfo(Kvittering kvittering) {
        String temakode = kvittering.sakstema;

        String behandlingstidTekst = cms.hentTekst("soknader.normertbehandlingstid");
        String key = "soknader.normertbehandlingstid." + temakode;

        String behandlingstid;
        if (cms.eksistererTekst(key)) {
            behandlingstid = cms.hentTekst(key);
        } else {
            logger.warn("Behandlingstid er ikke satt for temakode " + temakode);
            behandlingstid = cms.hentTekst("soknader.normertbehandlingstid.default");
        }

        add(new Label("behandlingstid", format(behandlingstidTekst, behandlingstid)));
    }

    private void leggTilVedleggFeiletPoput() {
        modalWindow = new ModigModalWindow("vedleggFeiletPopup");
        modalWindow.setContent(new ModalAdvarselPanel(modalWindow, "Dokumentet er slettet", "Dette dokumentet kan ikke vises fordi det er slettet.", "Lukk"));
        add(modalWindow);
    }

    private void leggTilInnsendteVedlegg(Kvittering kvittering) {
        WebMarkupContainer innsendteVedlegg = new WebMarkupContainer("innsendteVedleggSection");
        innsendteVedlegg.add(visibleIf(of(!kvittering.innsendteDokumenter.isEmpty())));
        innsendteVedlegg.add(
                new Label("innsendteDokumenterHeader", cms.hentTekst("behandling.innsendte.dokumenter.header")),
                getDokumenterView("innsendteVedlegg", kvittering.innsendteDokumenter, false)
        );
        add(innsendteVedlegg);
    }

    private void leggTilManglendeVedlegg(Kvittering kvittering) {
        WebMarkupContainer manglendeVedlegg = new WebMarkupContainer("manglendeVedleggSection");
        manglendeVedlegg.add(visibleIf(of(!kvittering.manglendeDokumenter.isEmpty() && !kvittering.ettersending)));
        manglendeVedlegg.add(
                new Label("manglendeVedleggHeader", cms.hentTekst("behandling.manglende.dokumenter.header")),
                getDokumenterView("manglendeVedlegg", kvittering.manglendeDokumenter, true)
        );
        add(manglendeVedlegg);
    }

    private PropertyListView<Dokument> getDokumenterView(String listViewId, List<Dokument> dokumenter, final boolean visInnsendingsvalg) {
        return new PropertyListView<Dokument>(listViewId, dokumenter) {

            @Override
            protected void populateItem(ListItem<Dokument> item) {
                Dokument dokument = item.getModelObject();

                String dokumentTittel = kodeverk.getSkjematittelForSkjemanummer(dokument.kodeverkRef);
                if (kodeverk.isEgendefinert(dokument.kodeverkRef)) {
                    dokumentTittel += ": " + dokument.tilleggstittel;
                }
                if (visInnsendingsvalg) {
                    item.add(new Label("innsendingsvalg", cms.hentTekst("kvittering.innsendingsvalg." + dokument.innsendingsvalg)));
                }
                item.add(new Label("dokument", dokumentTittel));

                if (!visInnsendingsvalg) {
                    AjaxLink<Void> hentVedleggLenke = new AjaxLink<Void>("hent-vedlegg") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            final byte[] pdfSomBytes = getMockPdf();

                            if (pdfSomBytes.length > 0) {
                                ResourceStreamAjaxBehaviour resourceStreamAjaxBehavoiur = lagHentPdfAjaxBehaviour(pdfSomBytes);
                                add(resourceStreamAjaxBehavoiur);
                                resourceStreamAjaxBehavoiur.init(target);
                            } else {
                                modalWindow.show(target);
                            }
                        }
                    };

                    item.add(hentVedleggLenke);
                }
            }
        };
    }

    private ResourceStreamAjaxBehaviour lagHentPdfAjaxBehaviour(final byte[] pdfSomBytes) {
        return new ResourceStreamAjaxBehaviour() {
            @Override
            protected IResourceStream getResourceStream() {
                return new AbstractResourceStreamWriter() {
                    @Override
                    public void write(OutputStream output) throws IOException {
                        output.write(pdfSomBytes);
                    }
                };
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnLoadHeaderItem.forScript("addKvitteringsPanelEvents()"));
    }

    //TODO midlertidig. Byttes ut med ordentlig mock-oppsett når tjenesten kommer mer på plass
    private byte[] getMockPdf() {
        try {
            return IOUtils.toByteArray(getClass().getResourceAsStream("/mock/mock.pdf"));
        } catch (IOException e) {
            throw new RuntimeException("IOException ved henting av Mock PDFen", e);
        }
    }
}
