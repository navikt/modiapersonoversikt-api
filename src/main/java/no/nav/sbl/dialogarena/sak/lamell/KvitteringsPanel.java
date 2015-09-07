package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.wicket.component.modal.ModalAdvarselPanel;
import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.service.JoarkService;
import no.nav.sbl.dialogarena.sak.util.ResourceStreamAjaxBehaviour;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Dokument;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat.Feilmelding;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
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

    @Inject
    private JoarkService joarkService;

    private Logger logger = LoggerFactory.getLogger(KvitteringsPanel.class);
    private ModigModalWindow modalWindow;
    private String fnr;
    private static final DateTime HL4_2015_DATO = new DateTime(2014, 12, 9, 0, 0);
    private static final String ARKIVTEMA_BIDRAG = "BID";
    private final Kvittering kvittering;
    private String sakstemakode;

    public KvitteringsPanel(String id, String tittel, Model<Kvittering> kvitteringsModel, String fnr, String sakstemakode) {
        super(id, kvitteringsModel);
        this.fnr = fnr;
        this.sakstemakode = sakstemakode;
        kvittering = kvitteringsModel.getObject();

        int antallInnsendteVedlegg = kvittering.innsendteDokumenter.size();
        int totalAntallVedlegg = antallInnsendteVedlegg + kvittering.manglendeDokumenter.size();

        String dato = dateTime(kvittering.behandlingDato);
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
        add(new Label("behandlingstid", new Model<>(format(behandlingstidTekst, behandlingstid))).setEscapeModelStrings(false));
    }

    private void leggTilVedleggFeiletPoput() {
        modalWindow = new ModigModalWindow("vedleggFeiletPopup");
        modalWindow.setInitialHeight(300);
        add(modalWindow);
    }

    private void leggTilInnsendteVedlegg(Kvittering kvittering) {
        WebMarkupContainer innsendteVedlegg = new WebMarkupContainer("innsendteVedleggSection");
        innsendteVedlegg.add(visibleIf(of(!kvittering.innsendteDokumenter.isEmpty())));
        innsendteVedlegg.add(
                new Label("innsendteDokumenterHeader", cms.hentTekst("behandling.innsendte.dokumenter.header")),
                getDokumenterView("innsendteVedlegg", kvittering.journalpostId, kvittering.innsendteDokumenter, false, kvittering.behandlingDato)
        );
        add(innsendteVedlegg);
    }

    private void leggTilManglendeVedlegg(Kvittering kvittering) {
        WebMarkupContainer manglendeVedlegg = new WebMarkupContainer("manglendeVedleggSection");
        manglendeVedlegg.add(visibleIf(of(!kvittering.manglendeDokumenter.isEmpty() && !kvittering.ettersending)));
        manglendeVedlegg.add(
                new Label("manglendeVedleggHeader", cms.hentTekst("behandling.manglende.dokumenter.header")),
                getDokumenterView("manglendeVedlegg", kvittering.journalpostId, kvittering.manglendeDokumenter, true, kvittering.behandlingDato)
        );
        add(manglendeVedlegg);
    }

    private PropertyListView<Dokument> getDokumenterView(String listViewId, final String journalpostId, List<Dokument> dokumenter, final boolean visInnsendingsvalg, final DateTime opprettetDato) {
        return new PropertyListView<Dokument>(listViewId, dokumenter) {

            @Override
            protected void populateItem(ListItem<Dokument> item) {
                final Dokument dokument = item.getModelObject();
                String dokumentTittel = kodeverk.getSkjematittelForSkjemanummer(dokument.kodeverkRef);
                if (kodeverk.isEgendefinert(dokument.kodeverkRef)) {
                    dokumentTittel += ": " + dokument.tilleggstittel;
                }
                dokumentTittel += ".";

                if (visInnsendingsvalg) {
                    item.add(new Label("innsendingsvalg", cms.hentTekst("kvittering.innsendingsvalg." + dokument.innsendingsvalg)));
                }
                item.add(new Label("dokument", dokumentTittel));

                if (!visInnsendingsvalg) {
                    AjaxLink<Void> hentVedleggLenke = new AjaxLink<Void>("hent-vedlegg") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            hentOgVisDokument(target, journalpostId, dokument);
                        }
                    };
                    String lenkeVisningsTekst = hentVisDokumentTekst(dokument.hovedskjema);
                    hentVedleggLenke.add(new Label("saksoversikt.kvittering.visvedlegg", lenkeVisningsTekst));
                    hentVedleggLenke.add(new AttributeAppender("aria-label", lenkeVisningsTekst + ": " + dokumentTittel));
                    hentVedleggLenke.add(visibleIf(new Model<>(bleSendtInnEtter2014HL4(opprettetDato) && !erTemaBidrag())));
                    item.add(hentVedleggLenke);
                }
            }
        };
    }

    private void hentOgVisDokument(AjaxRequestTarget target, String journalpostId, Dokument dokument) {
        try {
            HentDokumentResultat hentetDokument = joarkService.hentDokument(journalpostId, dokument.arkivreferanse, fnr);
            if (hentetDokument.harTilgang && hentetDokument.pdfSomBytes.isSome()) {
                visVedlegg(target, hentetDokument.pdfSomBytes.get());
            } else {
                visFeilmeldingVindu(target, hentetDokument.feilmelding, hentetDokument.argumenterTilFeilmelding);
            }
        } catch (Exception e) {
            logger.error("Det skjedde en uventet feil i hentDokument-kallet mot Joark. " +
                    "Dette kan skyldes at tjenesten er nede eller at det skjer en feil i f. eks. datapower. " +
                    "Brukeren vil vises en generell feilmelding");

            visFeilmeldingVindu(target, Feilmelding.GENERELL_FEIL);
        }
    }

    private void visVedlegg(AjaxRequestTarget target, byte[] pdfSomBytes) {
        ResourceStreamAjaxBehaviour resourceStreamAjaxBehavoiur = lagHentPdfAjaxBehaviour(pdfSomBytes);
        add(resourceStreamAjaxBehavoiur);
        resourceStreamAjaxBehavoiur.open(target);
    }

    private void visFeilmeldingVindu(AjaxRequestTarget target, Feilmelding feilmelding, String... argumenter) {
        modalWindow.setContent(new ModalAdvarselPanel(modalWindow,
                cms.hentTekst(feilmelding.heading),
                format(cms.hentTekst(feilmelding.lead), argumenter),
                cms.hentTekst("modalvindu.advarsel.knapp")));
        modalWindow.show(target);
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

                    @Override
                    public String getContentType() {
                        return "application/pdf";
                    }
                };
            }
        };
    }

    private String hentVisDokumentTekst(boolean erHovedskjema) {
        return erHovedskjema ? cms.hentTekst("saksoversikt.kvittering.vissoknad") : cms.hentTekst("saksoversikt.kvittering.visvedlegg");
    }

    private boolean bleSendtInnEtter2014HL4(DateTime opprettetDato) {
        if (opprettetDato.isAfter(HL4_2015_DATO)) {
            return true;
        }
        return false;
    }

    private boolean erTemaBidrag() {
        return ARKIVTEMA_BIDRAG.equalsIgnoreCase(sakstemakode);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnLoadHeaderItem.forScript("addKvitteringsPanelEvents()"));
    }

}
