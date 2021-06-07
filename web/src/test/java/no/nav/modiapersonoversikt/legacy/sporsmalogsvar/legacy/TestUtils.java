package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.legacy;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Status;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TestUtils {

    public final static String ID_1 = "id1";
    public final static String ID_2 = "id2";
    public final static String ID_3 = "id3";
    public final static String ID_4 = "id4";

    public final static DateTime DATE_1 = new DateTime().minusDays(1);
    public final static DateTime DATE_2 = new DateTime().minusDays(2);
    public final static DateTime DATE_3 = new DateTime().minusDays(3);
    public final static DateTime DATE_4 = new DateTime().minusDays(4);

    public final static Temagruppe TEMAGRUPPE_1 = Temagruppe.ARBD;
    public final static Temagruppe TEMAGRUPPE_2 = Temagruppe.FMLI;
    public final static Temagruppe TEMAGRUPPE_3 = Temagruppe.HJLPM;

    public final static int TRAAD_LENGDE = 3;

    public static final String JOURNALFORT_ID = "journalfortId";
    public static final DateTime JOURNALFORT_DATO = DateTime.now().minusDays(1);
    public static final String JOURNALFORT_TEMA = "journalfortTema";
    public static final String JOURNALFORT_SAKSID = "journalfortSaksId1";

    public static Melding createMelding(String id, Meldingstype type, DateTime ferdigstiltDato, Temagruppe temagruppe, String traadId) {
        Melding melding = new Melding(id, type, ferdigstiltDato);
        melding.temagruppe = temagruppe.toString();
        melding.gjeldendeTemagruppe = temagruppe;
        melding.traadId = traadId;
        melding.status = Status.IKKE_BESVART;
        if (type != Meldingstype.SPORSMAL_SKRIFTLIG) {
            melding.kanal = "telefon";
        }
        return melding;
    }

    public static XMLHenvendelse createMelding(String id, XMLHenvendelseType type, DateTime dato, Temagruppe temagruppe, String traadId) {
        return new XMLHenvendelse()
                .withBehandlingsId(id)
                .withBehandlingskjedeId(traadId)
                .withHenvendelseType(type.name())
                .withAvsluttetDato(dato)
                .withGjeldendeTemagruppe(temagruppe.name());
    }

    public static Melding opprettMeldingEksempel() {
        return createMelding(ID_1, Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now(), TEMAGRUPPE_1, ID_1);
    }

    public static Melding opprettSamtalereferatEksempel() {
        return createMelding(ID_1, Meldingstype.SAMTALEREFERAT_TELEFON, DateTime.now(), TEMAGRUPPE_1, ID_1);
    }

    public static List<MeldingVM> createMeldingVMer() {
        MeldingVM melding1VM = new MeldingVM(createMelding(ID_1, Meldingstype.SPORSMAL_SKRIFTLIG, DATE_3, TEMAGRUPPE_1, ID_1), TRAAD_LENGDE);
        MeldingVM melding2VM = new MeldingVM(createMelding(ID_2, Meldingstype.SAMTALEREFERAT_OPPMOTE, DATE_2, TEMAGRUPPE_2, ID_1), TRAAD_LENGDE);
        MeldingVM melding3VM = new MeldingVM(createMelding(ID_3, Meldingstype.SAMTALEREFERAT_TELEFON, DATE_1, TEMAGRUPPE_3, ID_1), TRAAD_LENGDE);
        return new ArrayList<>(asList(melding1VM, melding2VM, melding3VM));
    }

    public static XMLHenvendelse lagXMLHenvendelse(String behandlingsId, String behandlingskjedeId, DateTime opprettetDato, DateTime lestDato, String henvendelseType, String eksternAktor, XMLMetadataListe XMLMetadataListe) {
        return new XMLHenvendelse()
                .withBehandlingsId(behandlingsId)
                .withBehandlingskjedeId(behandlingskjedeId)
                .withOpprettetDato(opprettetDato)
                .withLestDato(lestDato)
                .withHenvendelseType(henvendelseType)
                .withEksternAktor(eksternAktor)
                .withGjeldendeTemagruppe("ARBD")
                .withJournalfortInformasjon(
                        new XMLJournalfortInformasjon()
                                .withJournalfortDato(JOURNALFORT_DATO)
                                .withJournalfortTema(JOURNALFORT_TEMA)
                                .withJournalpostId(JOURNALFORT_ID)
                                .withJournalfortSaksId(JOURNALFORT_SAKSID)
                )
                .withMetadataListe(XMLMetadataListe);
    }

}
