package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.core.context.ModigSecurityConstants;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.core.domain.IdentType;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.GODKJENTE_TEMA_FOR_GENERELLE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.GODKJENT_FAGSYSTEM_FOR_GENERELLE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.SAKSTYPE_GENERELL;

public class TestUtils {

    public final static String SAKS_ID_1 = "11111111";
    public final static String SAKS_ID_2 = "22222222";
    public final static String SAKS_ID_3 = "33333333";
    public final static String SAKS_ID_4 = "44444444";

    public final static String ID_1 = "id1";
    public final static String ID_2 = "id2";
    public final static String ID_3 = "id3";
    public final static String ID_4 = "id4";

    public final static DateTime DATE_1 = new DateTime().minusDays(1);
    public final static DateTime DATE_2 = new DateTime().minusDays(2);
    public final static DateTime DATE_3 = new DateTime().minusDays(3);
    public final static DateTime DATE_4 = new DateTime().minusDays(4);

    public final static String TEMAGRUPPE_1 = "Arbeidssøker";
    public final static String TEMAGRUPPE_2 = "Barnebidrag";
    public final static String TEMAGRUPPE_3 = "Familie og barn";

    public final static int TRAAD_LENGDE = 3;

    public final static String TEMA_1 = GODKJENTE_TEMA_FOR_GENERELLE.get(0);
    public final static String TEMA_2 = GODKJENTE_TEMA_FOR_GENERELLE.get(1);
    public final static String TEMA_3 = GODKJENTE_TEMA_FOR_GENERELLE.get(2);

    public static final String JOURNALFORT_ID = "journalfortId";
    public static final DateTime JOURNALFORT_DATO = DateTime.now().minusDays(1);
    public static final String JOURNALFORT_TEMA = "journalfortTema";
    public static final String JOURNALFORT_SAKSID = "journalfortSaksId1";

    public static ArrayList<Sak> createMockSaksliste() {
        return new ArrayList<>(asList(
                createSak(SAKS_ID_1, TEMA_1, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak(SAKS_ID_2, TEMA_2, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(3)),
                createSak(SAKS_ID_3, TEMA_3, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(9)),
                createSak(SAKS_ID_4, TEMA_1, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(2))));
    }

    public static Sak createSak(String saksId, String temaKode, String fagsystemKode, String sakstype, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = saksId;
        sak.temaKode = temaKode;
        sak.fagsystemKode = fagsystemKode;
        if (sakstype.equals(SAKSTYPE_GENERELL)) {
            sak.sakstype = sakstype;
        } else {
            sak.sakstype = temaKode;
        }

        sak.opprettetDato = opprettet;
        return sak;
    }

    public static Melding createMelding(String id, Meldingstype type, DateTime opprettetDato, String temagruppe, String traadId) {
        Melding melding = new Melding(id, type, opprettetDato);
        melding.temagruppe = temagruppe;
        melding.traadId = traadId;
        melding.status = Status.IKKE_BESVART;
        if (type != Meldingstype.SPORSMAL_SKRIFTLIG) {
            melding.kanal = "telefon";
        }
        return melding;
    }

    public static Melding createMeldingMedJournalfortDato(String id, Meldingstype type, DateTime opprettetDato, String temagruppe, String traadId, DateTime journalfortDato) {
        Melding melding = new Melding(id, type, opprettetDato);
        melding.temagruppe = temagruppe;
        melding.traadId = traadId;
        melding.journalfortDato = journalfortDato;
        return melding;
    }

    public static Melding opprettMeldingEksempel() {
        return createMelding(ID_1, Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now(), TEMA_1, ID_1);
    }

    public static List<MeldingVM> createMeldingVMer() {
        MeldingVM melding1VM = new MeldingVM(createMelding(ID_1, Meldingstype.SPORSMAL_SKRIFTLIG, DATE_3, TEMAGRUPPE_1, ID_1), TRAAD_LENGDE);
        MeldingVM melding2VM = new MeldingVM(createMelding(ID_2, Meldingstype.SAMTALEREFERAT_OPPMOTE, DATE_2, TEMAGRUPPE_2, ID_1), TRAAD_LENGDE);
        MeldingVM melding3VM = new MeldingVM(createMelding(ID_3, Meldingstype.SAMTALEREFERAT_TELEFON, DATE_1, TEMAGRUPPE_3, ID_1), TRAAD_LENGDE);
        return new ArrayList<>(asList(melding1VM, melding2VM, melding3VM));
    }

    public static XMLHenvendelse lagXMLHenvendelse(String behandlingsId, String behandlingskjedeId, DateTime opprettetDato, DateTime lestDato, String henvendelseType, XMLMetadataListe XMLMetadataListe) {
        return new XMLHenvendelse()
                .withBehandlingsId(behandlingsId)
                .withBehandlingskjedeId(behandlingskjedeId)
                .withOpprettetDato(opprettetDato)
                .withLestDato(lestDato)
                .withHenvendelseType(henvendelseType)
                .withJournalfortInformasjon(
                        new XMLJournalfortInformasjon()
                                .withJournalfortDato(JOURNALFORT_DATO)
                                .withJournalfortTema(JOURNALFORT_TEMA)
                                .withJournalpostId(JOURNALFORT_ID)
                                .withJournalfortSaksId(JOURNALFORT_SAKSID)
                )
                .withMetadataListe(XMLMetadataListe);
    }

    public static void innloggetBrukerEr(String userId) {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        System.setProperty(ModigSecurityConstants.SYSTEMUSER_USERNAME, "srvHenvendelse");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(userId, IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

}
