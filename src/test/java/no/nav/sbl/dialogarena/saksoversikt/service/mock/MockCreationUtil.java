package no.nav.sbl.dialogarena.saksoversikt.service.mock;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHovedskjema;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Tema;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.DOKUMENTINNSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.Innsendingsvalg.LASTET_OPP;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.Innsendingsvalg.SEND_SENERE;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BEHANDLINGKVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BEHANDLING_DATO;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BEHANDLING_STATUS;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsStatus.OPPRETTET;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsType.KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering.JOURNALPOST_ID;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.BEHANDLINGS_ID;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.ETTERSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.HenvendelseStatus.UNDER_ARBEID;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.OPPRETTET_DATO;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.SKJEMANUMMER_REF;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Tema.SISTOPPDATERTEBEHANDLING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Tema.TEMAKODE;
import static org.joda.time.DateTime.now;

public class MockCreationUtil {

    public static final String TEMA_DAGPENGER_SKJEMA_1 = "NAV 04-01.03";
    public static final String TEMA_AAP_SKJEMA_1 = "NAV 11-13.06";

    public static Record<GenerellBehandling> createHendelse() {
        return new Record<GenerellBehandling>()
                .with(BEHANDLING_STATUS, OPPRETTET)
                .with(SKJEMANUMMER_REF, "generell-behandling-kodeverk-ref-mock")
                .with(BEHANDLINGKVITTERING, BEHANDLING)
                .with(ETTERSENDING, false)
                .with(GenerellBehandling.OPPRETTET_DATO, now())
                .with(BEHANDLING_DATO, now());
    }

    public static Record<Kvittering> createKvittering() {
        return new Record<Kvittering>()
                .with(BEHANDLINGS_ID, "123-behandlingsid")
                .with(BEHANDLING_STATUS, OPPRETTET)
                .with(JOURNALPOST_ID, "journalpostid")
                .with(ARKIVREFERANSE_ORIGINALKVITTERING, optional("123456"))
                .with(SKJEMANUMMER_REF, "kvittering-kodeverk-ref-mock")
                .with(BEHANDLINGKVITTERING, KVITTERING)
                .with(ETTERSENDING, false)
                .with(KVITTERINGSTYPE, DOKUMENTINNSENDING)
                .with(INNSENDTE_DOKUMENTER, asList(
                        new Record<Dokument>()
                                .with(KODEVERK_REF, "bla")
                                .with(INNSENDINGSVALG, LASTET_OPP)
                                .with(ARKIVREFERANSE, "1234")
                                .with(UUID, "123-abc")
                                .with(HOVEDSKJEMA, false),
                        new Record<Dokument>()
                                .with(KODEVERK_REF, "bla2")
                                .with(INNSENDINGSVALG, LASTET_OPP)
                                .with(ARKIVREFERANSE, "4321")
                                .with(UUID, "abc-123")
                                .with(HOVEDSKJEMA, false)
                ))
                .with(Kvittering.MANGLENDE_DOKUMENTER, asList(
                        new Record<Dokument>()
                                .with(KODEVERK_REF, "bla3")
                                .with(INNSENDINGSVALG, SEND_SENERE)
                                .with(HOVEDSKJEMA, false)
                ))
                .with(BEHANDLING_DATO, now());
    }

    public static Record<Tema> createTema() {
        return new Record<Tema>()
                .with(TEMAKODE, "DAG")
                .with(SISTOPPDATERTEBEHANDLING, createHendelse());
    }

    public static Record<Soknad> createSoknad() {
        return new Record<Soknad>()
                .with(KVITTERINGSTYPE, DOKUMENTINNSENDING)
                .with(Soknad.TYPE, HenvendelseType.DOKUMENTINNSENDING)
                .with(STATUS, UNDER_ARBEID)
                .with(SKJEMANUMMER_REF, "soknad-kodeverk-ref-mock")
                .with(SISTENDRET_DATO, now())
                .with(OPPRETTET_DATO, now());
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
