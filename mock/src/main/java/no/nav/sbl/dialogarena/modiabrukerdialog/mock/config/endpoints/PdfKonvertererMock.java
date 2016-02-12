package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.BildeErKorruptFault;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.PdfErKorruptFault;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.SanntidPdfKonvertererV1;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.meldinger.*;

public class PdfKonvertererMock {

    public static SanntidPdfKonvertererV1 createSanntidPdfKonverterMock(){
        return new SanntidPdfKonvertererV1() {
            @Override
            public KonverterPdf2BildeResponse konverterPdf2Bilde(KonverterPdf2BildeRequest konverterPdf2BildeRequest) throws PdfErKorruptFault {
                return new KonverterPdf2BildeResponse()
                        .withUid("123")
                        .withPages(4);
            }

            @Override
            public KonverterBilde2PdfResponse konverterBilde2Pdf(KonverterBilde2PdfRequest konverterBilde2PdfRequest) throws BildeErKorruptFault {
                return null;
            }

            @Override
            public PingResponse ping(PingRequest pingRequest) {
                return new PingResponse();
            }
        };
    }
}
