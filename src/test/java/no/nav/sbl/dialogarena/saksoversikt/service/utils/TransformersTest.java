package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import org.joda.time.DateMidnight;
import org.junit.Test;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers.SOKNAD_TIL_KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.DOKUMENTINNSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.Innsendingsvalg.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsType.KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.ETTERSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.SKJEMANUMMER_REF;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.BEHANDLINGSKJEDE_ID;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.BEHANDLINGS_ID;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.Innsendingsvalg.INNSENDT;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TransformersTest {

    private static final String FERDIG = "FERDIG";

    @Test
    public void henvendelseTransformer_should_mapProperties() {
//        String behandlingId = "behandlingId";
//        String kodeverkId = "hovedskjemaKodeverkId";
//        DateTime innsendtDato = now();
//        DateTime opprettetDato = now();
//        String type = XMLHenvendelseType.SEND_SOKNAD.name();
//
//        XMLHenvendelse soknad = new XMLHenvendelse()
//                .withBehandlingsId(behandlingId)
//                .withMetadataListe(new XMLMetadataListe().withMetadata(
//                        new XMLHovedskjema()
//                                .withSkjemanummer(kodeverkId)
//                ))
//                .withAvsluttetDato(innsendtDato)
//                .withHenvendelseType(type)
//                .withOpprettetDato(opprettetDato);
//
//        Record<Soknad> record = SOKNAD.transform(soknad);
//
//        assertEquals(behandlingId, record.get(BEHANDLINGS_ID));
//        assertFalse(record.get(ETTERSENDING));
//        assertEquals(FERDIG, record.get(STATUS).toString());
//        assertEquals(kodeverkId, record.get(SKJEMANUMMER_REF));
//        assertEquals(innsendtDato, record.get(INNSENDT_DATO));
//        assertEquals(opprettetDato, record.get(Soknad.OPPRETTET_DATO));
//        assertEquals(type, record.get(Soknad.TYPE).name());
    }

    @Test
    public void dokumentTransformer_should_mapProperties() {
//        String tilleggstittel = "tilleggstittel";
//        String kodeverkId = "kodeverkId";
//        XMLVedlegg dokumentforventning = new XMLVedlegg()
//                .withSkjemanummer(kodeverkId)
//                .withInnsendingsvalg(LASTET_OPP.name())
//                .withTilleggsinfo(tilleggstittel);
//
//        Record<Dokument> record = tilDokument("hovedskjemaid").transform(dokumentforventning);
//
//        assertEquals(tilleggstittel, record.get(TILLEGGSTITTEL));
//        assertEquals(LASTET_OPP, record.get(INNSENDINGSVALG));
//        assertFalse(record.get(HOVEDSKJEMA));
//        assertEquals(kodeverkId, record.get(KODEVERK_REF));
    }

    @Test
    public void skalSetteHovedskjemaTilTrueDersomDokumentetHarSammeKodeverksIdSomBlirSendtInnTilTransformer() {
//        String tilleggstittel = "tilleggstittel";
//        String kodeverkId = "kodeverkId";
//        XMLVedlegg dokumentforventning = new XMLVedlegg()
//                .withSkjemanummer(kodeverkId)
//                .withInnsendingsvalg(LASTET_OPP.name())
//                .withTilleggsinfo(tilleggstittel);
//
//        Record<Dokument> record = tilDokument("kodeverkId").transform(dokumentforventning);
//
//        assertEquals(tilleggstittel, record.get(TILLEGGSTITTEL));
//        assertEquals(LASTET_OPP, record.get(INNSENDINGSVALG));
//        assertTrue(record.get(HOVEDSKJEMA));
//        assertEquals(kodeverkId, record.get(KODEVERK_REF));
    }

    @Test
    public void skalTransformereEnSoknadTilKvittering() {
        Record<Dokument> innsendtHovedskjema = new Record<Dokument>().with(HOVEDSKJEMA, true).with(INNSENDINGSVALG, LASTET_OPP).with(KODEVERK_REF, "kodeverk1");
        Record<Dokument> innsendtVedlegg = new Record<Dokument>().with(HOVEDSKJEMA, false).with(INNSENDINGSVALG, INNSENDT).with(KODEVERK_REF, "kodeverk2");
        Record<Dokument> sendSenereVedlegg = new Record<Dokument>().with(HOVEDSKJEMA, false).with(INNSENDINGSVALG, SEND_SENERE).with(KODEVERK_REF, "kodeverk3");

        Record<Soknad> soknad = new Record<Soknad>()
                .with(BEHANDLINGS_ID, "123-behandlingsid")
                .with(BEHANDLINGSKJEDE_ID, "123-behandlingskjedeid")
                .with(Soknad.TYPE, DOKUMENTINNSENDING)
                .with(INNSENDT_DATO, DateMidnight.parse("2014-01-01").toDateTime())
                .with(SKJEMANUMMER_REF, "kvittering-kodeverk-ref-mock")
                .with(GenerellBehandling.ETTERSENDING, false)
                .with(DOKUMENTER, asList(innsendtHovedskjema, innsendtVedlegg, sendSenereVedlegg));

        Record<Kvittering> kvittering = SOKNAD_TIL_KVITTERING.transform(soknad);

        assertThat(kvittering.get(BEHANDLINGS_ID), is(soknad.get(BEHANDLINGS_ID)));
        assertThat(kvittering.get(BEHANDLINGSKJEDE_ID), is(soknad.get(BEHANDLINGSKJEDE_ID)));
        assertThat(kvittering.get(KVITTERINGSTYPE), is(soknad.get(Soknad.TYPE)));
        assertThat(kvittering.get(BEHANDLING_DATO), is(soknad.get(INNSENDT_DATO)));
        assertThat(kvittering.get(SKJEMANUMMER_REF), is(soknad.get(SKJEMANUMMER_REF)));
        assertThat(kvittering.get(BEHANDLING_STATUS), is(AVSLUTTET));
        assertThat(kvittering.get(BEHANDLINGKVITTERING), is(KVITTERING));
        assertThat(kvittering.get(ETTERSENDING), is(soknad.get(ETTERSENDING)));
        assertThat(kvittering.get(INNSENDTE_DOKUMENTER), contains(innsendtHovedskjema, innsendtVedlegg));
        assertThat(kvittering.get(MANGLENDE_DOKUMENTER), contains(sendSenereVedlegg));
    }

    @Test
    public void skalLeggeTilHovedskjemaPaaKvitteringHvisHovedskjemaErInnsendt() {
        Record<Dokument> innsendtHovedskjema = new Record<Dokument>().with(HOVEDSKJEMA, true).with(INNSENDINGSVALG, LASTET_OPP).with(KODEVERK_REF, "kodeverk1");

        Record<Soknad> soknad = new Record<Soknad>()
                .with(DOKUMENTER, asList(innsendtHovedskjema));

        Record<Kvittering> kvittering = SOKNAD_TIL_KVITTERING.transform(soknad);

        assertThat(kvittering.get(INNSENDTE_DOKUMENTER).size(), is(1));
        assertThat(kvittering.get(INNSENDTE_DOKUMENTER), contains(innsendtHovedskjema));
    }

    @Test
    public void skalIkkeLeggeTilHovedskjemaPaaKvitteringHvisHovedskjemaIkkeErInnsendt() {
        Record<Dokument> innsendtHovedskjema = new Record<Dokument>().with(HOVEDSKJEMA, true).with(INNSENDINGSVALG, SEND_SENERE).with(KODEVERK_REF, "kodeverk1");

        Record<Soknad> soknad = new Record<Soknad>()
                .with(DOKUMENTER, asList(innsendtHovedskjema));

        Record<Kvittering> kvittering = SOKNAD_TIL_KVITTERING.transform(soknad);

        assertThat(kvittering.get(INNSENDTE_DOKUMENTER).size(), is(0));
        assertThat(kvittering.get(MANGLENDE_DOKUMENTER).size(), is(0));
    }

}
