package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing_helpers;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.DokumentInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Dokumenttyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.Journalpost;
import org.joda.time.DateTime;

import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService.*;

public class JournalforingSporsmal extends Journalforing {
    public static Journalpost lagJournalforingSporsmal(Sak sak, Melding melding) {
        Journalpost journalpost = new Journalpost();
        journalpost.setKanal(lagKommunikasjonskanaler());
        journalpost.setSignatur(lagSignatur());
        journalpost.setArkivtema(lagArkivtema(sak));
        journalpost.getForBruker().add(lagPerson(melding.fnrBruker));
        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");
        journalpost.setDokumentDato(DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(DateTime.now()));
        journalpost.setGjelderSak(SakToJournalforingSak.INSTANCE.transform(sak));

        lagRelasjon(melding, journalpost);
        return journalpost;
    }

    private static void lagRelasjon(Melding melding, Journalpost journalpost){
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        byte[] pdfInnhold = PdfUtils.genererPdf(melding);
        dokumentinfoRelasjon.setJournalfoertDokument(lagJournalfoertDokumentInfoForSporsmal(pdfInnhold));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);

    }

    private static JournalfoertDokumentInfo lagJournalfoertDokumentInfoForSporsmal(byte[] pdf) {
        JournalfoertDokumentInfo
                journalfoertDokumentInfo = new JournalfoertDokumentInfo();

        // TODO hent inn kodeverk for feletene setKodevrksRef() og setKodeRef() som tilhører dokumenttyper-objektet
        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue(DOKUTYPE_INNGAENDE);

        journalfoertDokumentInfo.setDokumentType(dokumenttyper);
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(SPORSMAL_OG_SVAR);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_KODE_ES);
        journalfoertDokumentInfo.setSensitivitet(false);

        List<DokumentInnhold> beskriverInnhold = journalfoertDokumentInfo.getBeskriverInnhold();
        beskriverInnhold.add(PdfDokumentToUstrukturertInnholdConverter.INSTANCE.transform(pdf));

        // TODO få inn den egentlige tittelen her
        journalfoertDokumentInfo.setTittel("Dokumenttittel");

        return journalfoertDokumentInfo;
    }
}
