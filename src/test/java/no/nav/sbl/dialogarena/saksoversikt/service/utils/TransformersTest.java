package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import org.joda.time.DateMidnight;
import org.junit.Test;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers.SOKNAD_TIL_KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.DOKUMENTINNSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.Innsendingsvalg.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus.FERDIG_BEHANDLET;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType.KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.Innsendingsvalg.INNSENDT;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TransformersTest {

    private static final String FERDIG = "FERDIG";

    @Test
    public void skalTransformereEnSoknadTilKvittering() {
        DokumentFraHenvendelse innsendtHovedskjema = new DokumentFraHenvendelse().withErHovedskjema(true).withInnsendingsvalg(LASTET_OPP).withKodeverkRef("kodeverk1");
        DokumentFraHenvendelse innsendtVedlegg = new DokumentFraHenvendelse().withErHovedskjema(false).withInnsendingsvalg(INNSENDT).withKodeverkRef("kodeverk2");
        DokumentFraHenvendelse sendSenereVedlegg = new DokumentFraHenvendelse().withErHovedskjema(false).withInnsendingsvalg(SEND_SENERE).withKodeverkRef("kodeverk3");

        Soknad soknad = new Soknad()
                .withBehandlingsId("123-behandlingsid")
                .withBehandlingskjedeId("123-behandlingskjedeid")
                .withHenvendelseType(DOKUMENTINNSENDING)
                .withInnsendtDato(DateMidnight.parse("2014-01-01").toDateTime())
                .withSkjemanummerRef("kvittering-kodeverk-ref-mock")
                .withEttersending(false)
                .withDokumenter(asList(innsendtHovedskjema, innsendtVedlegg, sendSenereVedlegg));

        Behandling kvittering = SOKNAD_TIL_KVITTERING.transform(soknad);

        assertThat(kvittering.getBehandlingsId(), is(soknad.getBehandlingsId()));
        assertThat(kvittering.getBehandlingskjedeId(), is(soknad.getBehandlingskjedeId()));
        assertThat(kvittering.getKvitteringstype(), is(soknad.getType()));
        assertThat(kvittering.getBehandlingDato(), is(soknad.getInnsendtDato()));
        assertThat(kvittering.getSkjemanummerRef(), is(soknad.getSkjemanummerRef()));
        assertThat(kvittering.getBehandlingsStatus(), is(FERDIG_BEHANDLET));
        assertThat(kvittering.getBehandlingkvittering(), is(KVITTERING));
        assertThat(kvittering.getEttersending(), is(soknad.getEttersending()));
        assertThat(kvittering.getInnsendteDokumenter(), contains(innsendtHovedskjema, innsendtVedlegg));
        assertThat(kvittering.getManglendeDokumenter(), contains(sendSenereVedlegg));
    }

    @Test
    public void skalLeggeTilHovedskjemaPaaKvitteringHvisHovedskjemaErInnsendt() {
        DokumentFraHenvendelse innsendtHovedskjema = new DokumentFraHenvendelse().withErHovedskjema(true).withInnsendingsvalg(LASTET_OPP).withKodeverkRef("kodeverk1");

        Soknad soknad = new Soknad()
                .withDokumenter(asList(innsendtHovedskjema));

        Behandling kvittering = SOKNAD_TIL_KVITTERING.transform(soknad);

        assertThat(kvittering.getInnsendteDokumenter().size(), is(1));
        assertThat(kvittering.getInnsendteDokumenter(), contains(innsendtHovedskjema));
    }

    @Test
    public void skalIkkeLeggeTilHovedskjemaPaaKvitteringHvisHovedskjemaIkkeErInnsendt() {
        DokumentFraHenvendelse innsendtHovedskjema = new DokumentFraHenvendelse().withErHovedskjema(true).withInnsendingsvalg(SEND_SENERE).withKodeverkRef("kodeverk1");

        Soknad soknad = new Soknad()
                .withDokumenter(asList(innsendtHovedskjema));

        Behandling kvittering = SOKNAD_TIL_KVITTERING.transform(soknad);

        assertThat(kvittering.getInnsendteDokumenter().size(), is(0));
        assertThat(kvittering.getManglendeDokumenter().size(), is(0));
    }

}
