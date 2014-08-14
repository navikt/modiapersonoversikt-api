package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Aktoer;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Arkivtemaer;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.DokumentInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Dokumenttyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.EksternPart;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Kommunikasjonskanaler;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Kryssreferanse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.NorskIdent;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Personidenter;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Signatur;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.UstrukturertInnhold;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.ARKIV_FILTYPE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.DOKUMENTTYPE_NOTAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.FORELOPIG_DOKUMENTTITTEL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.FORELOPIG_PERSONIDENTIFIKATOR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.KOMMUNIKASJONSKANAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.KRYSSREFERANSE_KODE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.VARIANSFORMAT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JournalforingTest {

    public static final String FNR = "fnr";

    private class KlasseSomArverJournalforing extends Journalforing { }

    @Test
    public void skalLagePersonMedRiktigeFelter() {
        Person person = KlasseSomArverJournalforing.lagPerson(FNR);

        assertNotNull(person.getIdent());
        assertNotNull(person.getIdent().getType());
        assertThat(person.getIdent().getIdent(), is(FNR));
        assertThat(person.getIdent().getType().getValue(), is(FORELOPIG_PERSONIDENTIFIKATOR));
        assertThat(person.getIdent().getType().getKodeverksRef(), is(new Personidenter().getKodeverksRef()));
    }

    @Test
    public void skalLageEksternPartMedRiktigeFelter() {
        Person person = lagPersonMedFNRIdent();

        EksternPart eksternPart = KlasseSomArverJournalforing.lagEksternPart(person);

        Aktoer eksternAktoer = eksternPart.getEksternAktoer();
        assertTrue(eksternAktoer.equals(person));
    }

    private Person lagPersonMedFNRIdent() {
        Person person = new Person();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(FNR);
        person.setIdent(norskIdent);
        return person;
    }

    @Test
    public void skalLageKryssreferanseMedRiktigeFelter() {
        String journalfortPostIdForTilhorendeSporsmal = "journalpostid";

        Kryssreferanse kryssreferanse = KlasseSomArverJournalforing.lagKryssreferanse(journalfortPostIdForTilhorendeSporsmal);

        assertThat(kryssreferanse.getReferanseId(), is(journalfortPostIdForTilhorendeSporsmal));
        assertThat(kryssreferanse.getReferansekode(), is(KRYSSREFERANSE_KODE));
    }

    @Test
    public void skalLageKommunikasjonskanalerMedRiktigeFelter() {
        Kommunikasjonskanaler kommunikasjonskanaler = KlasseSomArverJournalforing.lagKommunikasjonskanaler();

        assertThat(kommunikasjonskanaler.getValue(), is(KOMMUNIKASJONSKANAL));
    }

    @Test
    public void skalLageArkivtemaMedRiktigeFelter() {
        Sak sak = new Sak();
        sak.tema = "tema";

        Arkivtemaer arkivtemaer = KlasseSomArverJournalforing.lagArkivtema(sak);

        assertThat(arkivtemaer.getValue(), is(sak.tema));
    }

    @Test
    public void skalLageSignaturMedRiktigeFelter() {
        Signatur signatur = KlasseSomArverJournalforing.lagSignatur();

        assertThat(signatur.isSignert(), is(true));
    }

    @Test
    public void skalLageDokumenttypeMedRiktigeFelter() {
        String type = "type";

        Dokumenttyper dokumenttyper = KlasseSomArverJournalforing.lagDokumenttype(type);

        assertThat(dokumenttyper.getValue(), is(type));
    }

    @Test
    public void skalLageDokumenttypeMedKodeverksrefDersomNotatSendesInn() {
        Dokumenttyper dokumenttyper = KlasseSomArverJournalforing.lagDokumenttype(DOKUMENTTYPE_NOTAT);

        assertThat(dokumenttyper.getKodeRef(), is(dokumenttyper.getKodeverksRef()));
    }

    @Test
    public void skalLeggeBeskriverInnholdTilJournalfortDokumentInfo() {
        List<DokumentInnhold> beskriverInnhold = new ArrayList<>();
        byte[] byteliste = new byte[2];

        KlasseSomArverJournalforing.leggBeskriverInnholdTilJournalfortDokumentInfo(beskriverInnhold, byteliste);

        assertThat(beskriverInnhold.size(), is(1));
        assertThat(beskriverInnhold.get(0).getFiltype().getValue(), is(ARKIV_FILTYPE));
    }

    @Test
    public void skalTransformerePdfDokumentToUstrukturertInnhold() {
        byte[] byteliste = new byte[2];

        UstrukturertInnhold ustrukturertInnhold =
                KlasseSomArverJournalforing.PdfDokumentToUstrukturertInnholdConverter.INSTANCE.transform(byteliste);

        assertThat(ustrukturertInnhold.getFilnavn(), is(FORELOPIG_DOKUMENTTITTEL));
        assertThat(ustrukturertInnhold.getFiltype().getValue(), is(ARKIV_FILTYPE));
        assertThat(ustrukturertInnhold.getVariantformat().getValue(), is(VARIANSFORMAT));
        assertThat(ustrukturertInnhold.getInnhold(), is(byteliste));
    }

    @Test
    public void skalTransformereSakTilJournalforingssak() {
        Sak sak = new Sak();
        sak.saksId = "saksid";
        sak.fagsystem = "fagsystem";

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalforingssak
                = KlasseSomArverJournalforing.SakToJournalforingSak.INSTANCE.transform(sak);

        assertThat(journalforingssak.getSaksId(), is(sak.saksId));
        assertThat(journalforingssak.getFagsystemkode(), is(sak.fagsystem));
    }

    @Test
    public void skalTransformereDateTimeToXmlGregorianCalendar() {
        DateTime now = DateTime.now();

        XMLGregorianCalendar gregorianCalendar =
                KlasseSomArverJournalforing.DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(now);

        assertThat(gregorianCalendar.getYear(), is(now.getYear()));
        assertThat(gregorianCalendar.getDay(), is(now.getDayOfMonth()));
        assertThat(gregorianCalendar.getHour(), is(now.getHourOfDay()));
    }

}
