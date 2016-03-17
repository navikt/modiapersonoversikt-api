package no.nav.sbl.dialogarena.saksoversikt.service.mock;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHovedskjema;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Tema;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.Innsendingsvalg.LASTET_OPP;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.Innsendingsvalg.SEND_SENERE;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus.UNDER_BEHANDLING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType.KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.optional;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.DOKUMENTINNSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.HenvendelseStatus.UNDER_ARBEID;
import static org.joda.time.DateTime.now;

public class MockCreationUtil {

    public static final String TEMA_DAGPENGER_SKJEMA_1 = "NAV 04-01.03";
    public static final String TEMA_AAP_SKJEMA_1 = "NAV 11-13.06";

    public static Behandling createHendelse() {
        return new Behandling()
                .withBehandlingStatus(UNDER_BEHANDLING)
                .withSkjemanummerRef("generell-behandling-kodeverk-ref-mock")
                .withBehandlingKvittering(BEHANDLING)
                .withEttersending(false)
                .withOpprettetDato(now())
                .withBehandlingsDato(now());
    }

    public static Behandling createKvittering() {
        return new Behandling()
                .withBehandlingsId("123-behandlingsid")
                .withBehandlingStatus(UNDER_BEHANDLING)
                .withJournalPostId("journalpostid")
                .withArkivreferanseOriginalkvittering(optional("123456"))
                .withSkjemanummerRef("kvittering-kodeverk-ref-mock")
                .withBehandlingKvittering(KVITTERING)
                .withEttersending(false)
                .withKvitteringType(DOKUMENTINNSENDING)
                .withInnsendteDokumenter(asList(
                        new DokumentFraHenvendelse()
                                .withKodeverkRef("bla")
                                .withInnsendingsvalg(LASTET_OPP)
                                .withArkivreferanse("1234")
                                .withUuid("123-abc")
                                .withErHovedskjema(false),
                        new DokumentFraHenvendelse()
                                .withKodeverkRef("bla2")
                                .withInnsendingsvalg(LASTET_OPP)
                                .withArkivreferanse("4321")
                                .withUuid("abc-123")
                                .withErHovedskjema(false)
                ))
                .withManglendeDokumenter(asList(
                        new DokumentFraHenvendelse()
                                .withKodeverkRef("bla3")
                                .withInnsendingsvalg(SEND_SENERE)
                                .withErHovedskjema(false)
                ))
                .withBehandlingsDato(now());
    }

    public static Tema createTema() {
        return new Tema()
                .withTemakode("DAG")
                .withSistoppdatertebehandling(createHendelse());
    }

    public static Soknad createSoknad() {
        return new Soknad()
                .withHenvendelseType(HenvendelseType.DOKUMENTINNSENDING)
                .withStatus(UNDER_ARBEID)
                .withSkjemanummerRef("soknad-kodeverk-ref-mock")
                .withSistEndretDato(now())
                .withOpprettetDato(now());
    }

    public static WSSak createWSSak() {
        return new WSSak()
                .withSaksId("saksId-mock")
                .withSakstema(new WSSakstemaer().withValue("DAG").withKodeverksRef("kodeverk-ref-mock"))
                .withOpprettet(now());
    }

    public static WSBehandlingskjede createWSBehandlingskjede() {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("behandlingskjedeid-mock")
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withKodeverksRef("kodeverk-ref-mock"))
                .withBehandlingstema(new WSBehandlingstemaer().withKodeverksRef("kodeverk-tema-mock"))
                .withStart(now())
                .withSisteBehandlingREF("siste-behandling-ref-mock")
                .withSisteBehandlingstype(new WSBehandlingstyper().withKodeverksRef("behandlingstype-ref-mock"))
                .withSisteBehandlingsstegREF("siste-behandling-steg-ref-mock")
                .withSisteBehandlingsstegtype(new WSBehandlingsstegtyper().withKodeverksRef("behandlingssteg-ref-mock"));
    }

    public static XMLHenvendelse createWSSoknad(XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withBehandlingsId("behandlingid-mock")
                .withHenvendelseType(type.toString())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLHovedskjema()
                                .withSkjemanummer("hovedskjema-kodeverkid-mock")
                ))
                .withOpprettetDato(now())
                .withAvsluttetDato(now());
    }
}
