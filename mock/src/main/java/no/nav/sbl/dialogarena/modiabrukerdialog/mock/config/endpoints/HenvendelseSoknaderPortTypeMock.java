package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.FERDIG;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType.DOKUMENTINNSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType.SOKNADSINNSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSInnsendingsvalg.INNSENDT;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSInnsendingsvalg.SENDES_IKKE;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSInnsendingsvalg.SEND_SENERE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class HenvendelseSoknaderPortTypeMock {

    public static final String IKKE_KVITTERING = "ikke-kvittering";
    public static final String KVITTERING1 = "kvittering1";
    public static final String KVITTERING2 = "kvittering2";
    public static final String KVITTERING3 = "kvittering3";
    public static final String JOURNALPOSTID_FEILREGISTRERT_SAK = "1";
    public static final String JOURNALPOSTID_IKKE_JOURNALFORT = "2";
    public static final String JOURNALPOSTID_IKKE_SAKSPART = "3";
    public static final String JOURNALPOSTID_DOKUMENT_SLETTET = "4";
    public static final String JOURNALPOSTID_DEFAULT = "journalpostId";

    public static final String KVITTERINGETTERSENDELSE1 = "kvitteringEttersendelse1";
    public static final String KVITTERINGETTERSENDELSE2 = "kvitteringEttersendelse2";
    public static final String BEHANDLINGSKJEDEID_1 = "behandlingskjedeid-1";
    public static final String BEHANDLINGSKJEDEID_2 = "behandlingskjedeid-2";
    public static final String BEHANDLINGSKJEDEID_3 = "behandlingskjedeid-3";
    public static final DateTime MERGET_OPPRETTET = new DateTime().minusDays(110);
    public static final String TITTEL = "abc";

    @Bean
    public HenvendelseSoknaderPortType getHenvendelseSoknaderPortTypeMock() {
        HenvendelseSoknaderPortType mock = mock(HenvendelseSoknaderPortType.class);
        when(mock.hentSoknadListe(anyString())).thenReturn(mocklist());
        return mock;
    }

    private List<WSSoknad> mocklist() {
        return asList(
                kvitteringAAP(),
                kvitteringDAG(),

                kvitteringEttersendingAAP(),
                kvitteringEttersendingDAG()
        );
    }

    private static WSSoknad kvitteringEttersendingDAG() {
        return new WSSoknad()
                .withBehandlingsId(KVITTERINGETTERSENDELSE2)
                .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_2)
                .withJournalpostId(JOURNALPOSTID_DEFAULT)
                .withEttersending(true)
                .withHenvendelseType(SOKNADSINNSENDING.value())
                .withHenvendelseStatus(FERDIG.value())
                .withOpprettetDato(new DateTime())
                .withSistEndretDato(new DateTime())
                .withInnsendtDato(new DateTime())
                .withHovedskjemaKodeverkId("NAV 04-01.03")
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger().withDokumentforventning(
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-16.03").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.04").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-01.03").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.03").withTilleggsTittel(TITTEL)
                ));
    }

    private static WSSoknad kvitteringEttersendingAAP() {
        return new WSSoknad()
                .withBehandlingsId(KVITTERINGETTERSENDELSE1)
                .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_1)
                .withJournalpostId(JOURNALPOSTID_FEILREGISTRERT_SAK)
                .withEttersending(true)
                .withHenvendelseType(DOKUMENTINNSENDING.value())
                .withHenvendelseStatus(FERDIG.value())
                .withSistEndretDato(new DateTime())
                .withOpprettetDato(new DateTime())
                .withInnsendtDato(new DateTime())
                .withHovedskjemaKodeverkId("NAV 11-13.05")
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger().withDokumentforventning(
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 11-13.05").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.04").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-01.03").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.03").withTilleggsTittel(TITTEL)
                ));
    }

    private static WSSoknad kvitteringDAG() {
        return new WSSoknad()
                .withBehandlingsId(KVITTERING2)
                .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_2)
                .withJournalpostId(JOURNALPOSTID_IKKE_JOURNALFORT)
                .withEttersending(false)
                .withHenvendelseType(SOKNADSINNSENDING.value())
                .withHenvendelseStatus(FERDIG.value())
                .withOpprettetDato(MERGET_OPPRETTET)
                .withSistEndretDato(new DateTime())
                .withInnsendtDato(new DateTime().minusDays(3))
                .withHovedskjemaKodeverkId("NAV 04-01.03")
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger().withDokumentforventning(
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-01.03").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.04").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(SEND_SENERE.value()).withKodeverkId("NAV 04-16.03").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(SEND_SENERE.value()).withKodeverkId("NAV 04-08.03").withTilleggsTittel(TITTEL)
                ));
    }

    private static WSSoknad kvitteringAAP() {
        return new WSSoknad()
                .withBehandlingsId(KVITTERING1)
                .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_1)
                .withJournalpostId(JOURNALPOSTID_IKKE_SAKSPART)
                .withEttersending(false)
                .withHenvendelseType(DOKUMENTINNSENDING.value())
                .withHenvendelseStatus(FERDIG.value())
                .withOpprettetDato(MERGET_OPPRETTET)
                .withSistEndretDato(new DateTime())
                .withInnsendtDato(new DateTime().minusDays(3))
                .withHovedskjemaKodeverkId("NAV 11-13.05")
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger().withDokumentforventning(
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 11-13.05").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.04").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("L7").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(SENDES_IKKE.value()).withKodeverkId("NAV 04-01.03").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(SENDES_IKKE.value()).withKodeverkId("NAV 04-08.03").withTilleggsTittel(TITTEL)
                ));
    }


    private static WSSoknad kvitteringForeldrepenger() {
        return new WSSoknad()
                .withBehandlingsId(KVITTERING3)
                .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_3)
                .withJournalpostId(JOURNALPOSTID_DOKUMENT_SLETTET)
                .withEttersending(false)
                .withHenvendelseType(SOKNADSINNSENDING.value())
                .withHenvendelseStatus(FERDIG.value())
                .withOpprettetDato(MERGET_OPPRETTET)
                .withSistEndretDato(new DateTime())
                .withInnsendtDato(new DateTime().minusDays(2))
                .withHovedskjemaKodeverkId("NAV 14-05.09")
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger().withDokumentforventning(
                        new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 14-05.09").withTilleggsTittel(TITTEL),
                        new WSDokumentforventning().withInnsendingsvalg(SEND_SENERE.value()).withKodeverkId("L7").withTilleggsTittel(TITTEL)
                ));
    }
}
