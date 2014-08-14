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
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSInnsendingsvalg.SEND_SENERE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class HenvendelseSoknaderPortTypeMock {

    public static final String IKKE_KVITTERING = "ikke-kvittering";
    public static final String KVITTERING1 = "kvittering1";
    public static final String KVITTERING2 = "kvittering2";
    public static final String BEHANDLINGSKJEDEID_1 = "behandlingskjedeid-1";
    public static final String BEHANDLINGSKJEDEID_2 = "behandlingskjedeid-1";
    public static final DateTime MERGET_OPPRETTET = new DateTime().minusDays(110);

    @Bean
    public HenvendelseSoknaderPortType getHenvendelseSoknaderPortTypeMock() {
        HenvendelseSoknaderPortType mock = mock(HenvendelseSoknaderPortType.class);
        when(mock.hentSoknadListe(anyString())).thenReturn(mocklist());
        return mock;
    }

    private List<WSSoknad> mocklist() {
        return asList(
                new WSSoknad()
                        .withBehandlingsId(KVITTERING1)
                        .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_1)
                        .withEttersending(false)
                        .withHenvendelseType(DOKUMENTINNSENDING.value())
                        .withHenvendelseStatus(FERDIG.value())
                        .withOpprettetDato(MERGET_OPPRETTET)
                        .withInnsendtDato(new DateTime())
                        .withHovedskjemaKodeverkId("hovedskjema")
                        .withDokumentforventninger(
                                new WSSoknad.Dokumentforventninger()
                                        .withDokumentforventning(
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-16.03").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.04").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(SEND_SENERE.value()).withKodeverkId("NAV 04-01.03").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(SEND_SENERE.value()).withKodeverkId("NAV 04-08.03").withTilleggsTittel("abc")
                                        )),
                new WSSoknad()
                        .withBehandlingsId(KVITTERING2)
                        .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_2)
                        .withEttersending(false)
                        .withHenvendelseType(SOKNADSINNSENDING.value())
                        .withHenvendelseStatus(FERDIG.value())
                        .withOpprettetDato(MERGET_OPPRETTET)
                        .withInnsendtDato(new DateTime())
                        .withHovedskjemaKodeverkId("hovedskjema")
                        .withDokumentforventninger(
                                new WSSoknad.Dokumentforventninger()
                                        .withDokumentforventning(
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-16.03").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("NAV 04-08.04").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(SEND_SENERE.value()).withKodeverkId("NAV 04-01.03").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(SEND_SENERE.value()).withKodeverkId("NAV 04-08.03").withTilleggsTittel("abc")
                                        )),
                new WSSoknad().withHenvendelseStatus(UNDER_ARBEID.value()).withBehandlingsId(IKKE_KVITTERING),
                new WSSoknad().withHenvendelseStatus(UNDER_ARBEID.value()).withBehandlingsId(IKKE_KVITTERING)
        );
    }

}
