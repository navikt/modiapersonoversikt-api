package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.sak.viewdomain.detalj.TjenesteResultatWrapper;

public interface PDFConverterService {

    TjenesteResultatWrapper konverterPDFTilBildeurler(byte[] pdf);
}
