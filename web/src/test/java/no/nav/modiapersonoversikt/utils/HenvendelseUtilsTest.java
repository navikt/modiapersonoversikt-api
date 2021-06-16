package no.nav.modiapersonoversikt.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Fritekst;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

public class HenvendelseUtilsTest {

    @Test
    public void skalLageXMLHenvendelseObjektMedMeldingTilBruker() {
        XMLHenvendelseType svartype = XMLHenvendelseType.SVAR_SKRIFTLIG;
        Melding svar = createSvarEllerReferat();

        XMLHenvendelse xmlHenvendelse = HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker(svar, svartype);

        assertThat(xmlHenvendelse.getHenvendelseType(), is(svartype.name()));
        assertThat(xmlHenvendelse.getFnr(), is(svar.fnrBruker));
        assertThat(xmlHenvendelse.isErTilknyttetAnsatt(), is(svar.erTilknyttetAnsatt));
        assertNotNull(xmlHenvendelse.getOpprettetDato());
        assertNotNull(xmlHenvendelse.getAvsluttetDato());

        XMLMeldingTilBruker metadata = (XMLMeldingTilBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        assertThat(xmlHenvendelse.getBehandlingskjedeId(), is(svar.traadId));
        assertThat(metadata.getTemagruppe(), is(svar.temagruppe));
        assertThat(metadata.getKanal(), is(svar.kanal));
        assertThat(metadata.getFritekst(), is(svar.getFritekst()));
        assertThat(metadata.getNavident(), is(svar.navIdent));
    }


    private Melding createSvarEllerReferat() {
        return new Melding()
                .withFnr("fnr")
                .withTraadId("sporsmalid")
                .withTemagruppe("temagruppe")
                .withKanal("kanal")
                .withFritekst(new Fritekst("fritekst", new Saksbehandler("", "", ""), new DateTime()))
                .withNavIdent("navident")
                .withErTilknyttetAnsatt(false);
    }
}
