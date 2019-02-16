package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.PdfErKorruptFault;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.SanntidPdfKonvertererV1;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.meldinger.KonverterPdf2BildeRequest;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.meldinger.KonverterPdf2BildeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.KORRUPT_PDF;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.PDF_KONVERTERING;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;
import static no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.informasjon.WSKonverterFra.FOERSTE_SIDE;

public class PDFConverterService {

    private static final Logger logger = LoggerFactory.getLogger(SanntidPdfKonvertererV1.class);

    @Inject
    private SanntidPdfKonvertererV1 sanntidPdfKonverterV1;

    public TjenesteResultatWrapper konverterPDFTilBildeurler(byte[] pdf){
        try {
            KonverterPdf2BildeResponse response = sanntidPdfKonverterV1.konverterPdf2Bilde(
                    new KonverterPdf2BildeRequest()
                            .withPdf(pdf)
                            .withKonverterFra(FOERSTE_SIDE));

            List<String> pdfURLer = new ArrayList<>();

            for (int i = 0; i < response.getPages(); i++) {
                pdfURLer.add(System.getProperty("tjenester.url") + System.getProperty("pdfkonverterer.sti") + response.getUid() + "/" + (i+1));
            }
            return new TjenesteResultatWrapper(pdfURLer);
        } catch (PdfErKorruptFault e) {
            logger.warn("PDF er korrupt!", e);
            return new TjenesteResultatWrapper(KORRUPT_PDF);
        } catch (RuntimeException e) {
            logger.error("Feil mot konverteringstjenesten", e);
            throw new FeilendeBaksystemException(PDF_KONVERTERING);
        }
    }
}
