package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.print;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.time.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PrintLenke extends DownloadLink {

    public PrintLenke(String id, IModel<List<MeldingVM>> model) {
        super(id, new PrintFilModel(model), "meldinger.pdf");

        setCacheDuration(Duration.NONE);
        setDeleteAfterDownload(true);
    }

    private static final class PrintFilModel extends LoadableDetachableModel<File> {

        private IModel<List<MeldingVM>> model;

        private PrintFilModel(IModel<List<MeldingVM>> model) {
            this.model = model;
        }

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
            List<MeldingVM> meldinger = model.getObject();
            return PdfUtils.genererPdfForPrintVM(meldinger);
        }
    }
}
