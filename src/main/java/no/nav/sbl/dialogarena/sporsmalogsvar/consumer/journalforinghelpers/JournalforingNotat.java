package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforinghelpers;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.DokumentInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Dokumenttyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost;
import org.joda.time.DateTime;

import java.util.List;

public class JournalforingNotat extends Journalforing {
    public static Journalpost lagJournalforingNotat(Optional<String> journalfortPostId, Sak sak, Melding melding) {
        Journalpost journalpost = new Journalpost();
        journalpost.setKanal(lagKommunikasjonskanaler());
        journalpost.setSignatur(lagSignatur());
        journalpost.setArkivtema(lagArkivtema(sak));
        journalpost.getForBruker().add(lagPerson(melding.fnrBruker));
        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");
        journalpost.setDokumentDato(DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(DateTime.now()));
        journalpost.setGjelderSak(SakToJournalforingSak.INSTANCE.transform(sak));

        if (journalfortPostId.isSome()) {
            journalpost.getKryssreferanseListe().add(lagKryssreferanse(journalfortPostId.get()));
        }
        lagRelasjon(melding, journalpost);
        return journalpost;
    }

    private static void lagRelasjon(Melding melding, Journalpost journalpost) {
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        byte[] pdfInnhold = PdfUtils.genererPdf(melding);
        dokumentinfoRelasjon.setJournalfoertDokument(lagJournalfoertDokumentInfoForNotat(pdfInnhold));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);
    }

    private static JournalfoertDokumentInfo lagJournalfoertDokumentInfoForNotat(byte[] pdf) {
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();

        // TODO hent inn kodeverk for feletene setKodevrksRef() og setKodeRef() som tilhører dokumenttyper-objektet
        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue(DOKUTYPE_UTGAENDE);

        journalfoertDokumentInfo.setDokumentType(dokumenttyper);
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(SPORSMAL_OG_SVAR);
        journalfoertDokumentInfo.setErOrganinternt(false);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_KODE_ES);
        journalfoertDokumentInfo.setSensitivitet(false);

        List<DokumentInnhold> beskriverInnhold = journalfoertDokumentInfo.getBeskriverInnhold();
        beskriverInnhold.add(PdfDokumentToUstrukturertInnholdConverter.INSTANCE.transform(pdf));

        // TODO få inn den egentlige tittelen her
        journalfoertDokumentInfo.setTittel("Dokumenttittel");

        return journalfoertDokumentInfo;
    }

}
