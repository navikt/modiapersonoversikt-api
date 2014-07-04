package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSInnsendingsvalg;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.joda.time.DateTime;
import org.junit.Test;

import static no.nav.sbl.dialogarena.sak.mock.HenvendelseMocks.createWSDokumentforventning;
import static no.nav.sbl.dialogarena.sak.mock.HenvendelseMocks.createWSSoknad;
import static no.nav.sbl.dialogarena.sak.transformers.HenvendelseTransformers.INNSENDT;
import static no.nav.sbl.dialogarena.sak.transformers.HenvendelseTransformers.KVITTERING;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HenvendelseTransformersTest {

    @Test
    public void innsendtTransformerGirTrue_naarInnsendtDato_erSatt() {
        WSSoknad innsendtSoknad = createWSSoknad().withInnsendtDato(new DateTime());
        assertThat(INNSENDT.transform(innsendtSoknad), equalTo(true));
    }

    @Test
    public void innsendtTransformerGirFalse_naarInnsendtDato_ikkeErSatt() {
        WSSoknad innsendtSoknad = createWSSoknad().withInnsendtDato(null);
        assertThat(INNSENDT.transform(innsendtSoknad), equalTo(false));
    }

    @Test
    public void kvitteringTransformerKomplettObjektMapping() {
        String id = "behandlingsIdForTest";
        String kjedeId = "behandlingsKjedeIdForTest";
        String hovedskjema = "hovedskjemaKodeverkIdForTest";
        WSSoknad wsKvittering = createWSSoknad()
                .withInnsendtDato(new DateTime())
                .withBehandlingsId(id)
                .withBehandlingsKjedeId(kjedeId)
                .withHovedskjemaKodeverkId(hovedskjema)
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger()
                        .withDokumentforventning(createWSDokumentforventning().withInnsendingsvalg(WSInnsendingsvalg.INNSENDT.name()))
                        .withDokumentforventning(createWSDokumentforventning().withInnsendingsvalg(WSInnsendingsvalg.INNSENDT.name()))
                        .withDokumentforventning(createWSDokumentforventning().withInnsendingsvalg(WSInnsendingsvalg.SEND_SENERE.name()))
                );
        Kvittering kvittering = KVITTERING.transform(wsKvittering);
        assertThat(kvittering.behandlingsId, equalTo(id));
        assertThat(kvittering.behandlingskjedeId, equalTo(kjedeId));
        assertThat(kvittering.skjemanummerRef, equalTo(hovedskjema));
        assertThat(kvittering.innsendteDokumenter.size(), equalTo(2));
        assertThat(kvittering.manglendeDokumenter.size(), equalTo(1));
    }

}
