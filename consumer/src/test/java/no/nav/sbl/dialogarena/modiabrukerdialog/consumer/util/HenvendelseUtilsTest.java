package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;
import org.joda.time.DateTime;
import org.junit.Test;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

public class HenvendelseUtilsTest {

    @Test
    public void skalLageXMLHenvendelseObjektMedMeldingTilBrukerBasertPaaSvarEllerReferatOgType() {
        XMLHenvendelseType svartype = XMLHenvendelseType.SVAR_SKRIFTLIG;
        SvarEllerReferat svar = createSvarEllerReferat();

        XMLHenvendelse xmlHenvendelse = HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker(svar, svartype);

        assertThat(xmlHenvendelse.getHenvendelseType(), is(svartype.name()));
        assertThat(xmlHenvendelse.getFnr(), is(svar.fnr));
        assertNotNull(xmlHenvendelse.getOpprettetDato());
        assertNotNull(xmlHenvendelse.getAvsluttetDato());

        XMLMeldingTilBruker metadata = (XMLMeldingTilBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        assertThat(xmlHenvendelse.getBehandlingskjedeId(), is(svar.sporsmalsId));
        assertThat(metadata.getTemagruppe(), is(svar.temagruppe));
        assertThat(metadata.getKanal(), is(svar.kanal));
        assertThat(metadata.getFritekst(), is(svar.fritekst));
        assertThat(metadata.getNavident(), is(svar.navIdent));
    }

    @Test
    public void skalLageSporsmalBasertPaaXMLHenvendelseMedXMLMeldingFraBruker() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingFraBruker();
        XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        Sporsmal sporsmal = HenvendelseUtils.createSporsmalFromXMLHenvendelse(xmlHenvendelse);

        assertThat(sporsmal.id, is(xmlHenvendelse.getBehandlingsId()));
        assertThat(sporsmal.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(sporsmal.oppgaveId, is(xmlHenvendelse.getOppgaveIdGsak()));
        assertThat(sporsmal.temagruppe, is(xmlMeldingFraBruker.getTemagruppe()));
        assertThat(sporsmal.fritekst, is(xmlMeldingFraBruker.getFritekst()));
    }

    @Test
    public void taklerAtInnholdetErKassert() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingFraBruker();
        xmlHenvendelse.setMetadataListe(null);

        Sporsmal sporsmal = HenvendelseUtils.createSporsmalFromXMLHenvendelse(xmlHenvendelse);

        assertThat(sporsmal.id, is(xmlHenvendelse.getBehandlingsId()));
        assertThat(sporsmal.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(sporsmal.oppgaveId, is(xmlHenvendelse.getOppgaveIdGsak()));
        assertThat(sporsmal.temagruppe, is(nullValue()));
        assertThat(sporsmal.fritekst, is(nullValue()));
    }

    @Test(expected = ApplicationException.class)
    public void skalKasteExeptionDersomManLagerSporsmalBasertPaaXMLHenvendelseMedNoeAnnetEnnXMLMeldingFraBruker() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG);

        HenvendelseUtils.createSporsmalFromXMLHenvendelse(xmlHenvendelse);
    }

    @Test
    public void lagerSvarEllerReferatFromXMLHenvendelseMedXMLMeldingTilBrukerSomReferat() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE);
        XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        SvarEllerReferat referat = HenvendelseUtils.createSvarEllerReferatFromXMLHenvendelse(xmlHenvendelse);

        assertThat(referat.fnr, is(xmlHenvendelse.getFnr()));
        assertThat(referat.type, is(SvarEllerReferat.Henvendelsetype.REFERAT_OPPMOTE));
        assertThat(referat.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(referat.sporsmalsId, is(xmlHenvendelse.getBehandlingskjedeId()));
        assertThat(referat.temagruppe, is(xmlMeldingTilBruker.getTemagruppe()));
        assertThat(referat.kanal, is(xmlMeldingTilBruker.getKanal()));
        assertThat(referat.fritekst, is(xmlMeldingTilBruker.getFritekst()));
        assertThat(referat.navIdent, is(xmlMeldingTilBruker.getNavident()));
    }

    @Test
    public void lagerSvarEllerReferatFromXMLHenvendelseMedXMLMeldingTilBrukerSomSvar() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG);
        XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        SvarEllerReferat svar = HenvendelseUtils.createSvarEllerReferatFromXMLHenvendelse(xmlHenvendelse);

        assertThat(svar.fnr, is(xmlHenvendelse.getFnr()));
        assertThat(svar.type, is(SvarEllerReferat.Henvendelsetype.SVAR_SKRIFTLIG));
        assertThat(svar.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(svar.sporsmalsId, is(xmlHenvendelse.getBehandlingskjedeId()));
        assertThat(svar.temagruppe, is(xmlMeldingTilBruker.getTemagruppe()));
        assertThat(svar.kanal, is(xmlMeldingTilBruker.getKanal()));
        assertThat(svar.fritekst, is(xmlMeldingTilBruker.getFritekst()));
        assertThat(svar.navIdent, is(xmlMeldingTilBruker.getNavident()));
    }

    @Test
    public void lagerReferatSelvOmInnholdErKassert() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG);
        xmlHenvendelse.setMetadataListe(null);

        SvarEllerReferat svar = HenvendelseUtils.createSvarEllerReferatFromXMLHenvendelse(xmlHenvendelse);

        assertThat(svar.fnr, is(xmlHenvendelse.getFnr()));
        assertThat(svar.type, is(SvarEllerReferat.Henvendelsetype.SVAR_SKRIFTLIG));
        assertThat(svar.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(svar.sporsmalsId, is(xmlHenvendelse.getBehandlingskjedeId()));
        assertThat(svar.temagruppe, is(nullValue()));
        assertThat(svar.kanal, is(nullValue()));
        assertThat(svar.fritekst, is(nullValue()));
        assertThat(svar.navIdent, is(nullValue()));
    }

    @Test
    public void lagerSvarSelvOmInnholdErKassert() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG);
        xmlHenvendelse.setMetadataListe(null);

        SvarEllerReferat svar = HenvendelseUtils.createSvarEllerReferatFromXMLHenvendelse(xmlHenvendelse);

        assertThat(svar.fnr, is(xmlHenvendelse.getFnr()));
        assertThat(svar.type, is(SvarEllerReferat.Henvendelsetype.SVAR_SKRIFTLIG));
        assertThat(svar.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(svar.sporsmalsId, is(xmlHenvendelse.getBehandlingskjedeId()));
        assertThat(svar.temagruppe, is(nullValue()));
        assertThat(svar.kanal, is(nullValue()));
        assertThat(svar.fritekst, is(nullValue()));
        assertThat(svar.navIdent, is(nullValue()));
    }

    @Test(expected = ApplicationException.class)
    public void skalKasteExceptionDersomManLagerSvarEllerReferatFromXMLHenvendelseMedNoeAnnetEnnXMLMeldingTilBruker() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingFraBruker();

        HenvendelseUtils.createSvarEllerReferatFromXMLHenvendelse(xmlHenvendelse);
    }

    private SvarEllerReferat createSvarEllerReferat() {
        return new SvarEllerReferat()
                .withFnr("fnr")
                .withSporsmalsId("sporsmalid")
                .withTemagruppe("temagruppe")
                .withKanal("kanal")
                .withFritekst("fritekst")
                .withNavIdent("navident");
    }

    private XMLHenvendelse createXMLHenvendelseMedXmlMeldingFraBruker() {
        return new XMLHenvendelse()
                .withBehandlingsId("behandlingsid")
                .withHenvendelseType(SPORSMAL_SKRIFTLIG.name())
                .withOpprettetDato(DateTime.now())
                .withOppgaveIdGsak("oppgaveidgsak")
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingFraBruker()
                                .withTemagruppe("temagruppe")
                                .withFritekst("fritekst")
                ));
    }

    private XMLHenvendelse createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withFnr("fnr")
                .withHenvendelseType(type.name())
                .withOpprettetDato(DateTime.now())
                .withBehandlingskjedeId("behandlingskjedeId")
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withTemagruppe("temagruppe")
                                .withKanal("kanal")
                                .withFritekst("fritekst")
                                .withNavident("navident")
                ));
    }

}
