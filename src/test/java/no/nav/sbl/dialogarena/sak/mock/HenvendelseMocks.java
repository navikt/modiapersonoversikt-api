package no.nav.sbl.dialogarena.sak.mock;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;

import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType.DOKUMENTINNSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSInnsendingsvalg.INNSENDT;
import static org.joda.time.DateTime.now;

public class HenvendelseMocks {

    public static WSDokumentforventning createWSDokumentforventning() {
        return new WSDokumentforventning()
                .withKodeverkId("kodeverk-ref-mock")
                .withInnsendingsvalg(INNSENDT.toString());
    }

    public static WSSoknad createWSSoknad() {
        return new WSSoknad()
                .withBehandlingsId("behandlingid-mock")
                .withHenvendelseType(DOKUMENTINNSENDING.toString())
                .withHovedskjemaKodeverkId("hovedskjema-kodeverkid-mock")
                .withHenvendelseStatus(UNDER_ARBEID.toString())
                .withOpprettetDato(now())
                .withEttersending(false);
    }

}
