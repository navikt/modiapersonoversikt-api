package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.SvarEllerReferat;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.SvarEllerReferat.Henvendelsetype.SVAR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.SvarEllerReferat.Henvendelsetype.REFERAT;
import static org.joda.time.DateTime.now;

public class SakUtils {

    public static Sporsmal createSporsmalFromXMLHenvendelse(XMLHenvendelse henvendelse) {
        Sporsmal sporsmal = new Sporsmal(henvendelse.getBehandlingsId(), henvendelse.getOpprettetDato());

        XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
        if (xmlMetadata instanceof XMLMeldingFraBruker) {
            XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
            sporsmal.temagruppe = xmlMeldingFraBruker.getTemagruppe();
            sporsmal.fritekst = xmlMeldingFraBruker.getFritekst();
            sporsmal.oppgaveId = xmlMeldingFraBruker.getOppgaveIdGsak();
            return sporsmal;
        } else {
            throw new ApplicationException("Henvendelsen er ikke av typen XMLMeldingFraBruker: " + xmlMetadata);
        }
    }

    public static SvarEllerReferat createSvarEllerReferatFromXMLHenvendelse(XMLHenvendelse henvendelse) {
        XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
        if (xmlMetadata instanceof XMLMeldingTilBruker) {
            XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
            return new SvarEllerReferat()
                    .withType(henvendelse.getHenvendelseType().equals(XMLHenvendelseType.SVAR.name()) ? SVAR : REFERAT)
                    .withFnr(henvendelse.getFnr())
                    .withSporsmalsId(xmlMeldingTilBruker.getSporsmalsId())
                    .withTemagruppe(xmlMeldingTilBruker.getTemagruppe())
                    .withKanal(xmlMeldingTilBruker.getKanal())
                    .withFritekst(xmlMeldingTilBruker.getFritekst())
                    .withNavIdent(xmlMeldingTilBruker.getNavident())
                    .withOpprettetDato(henvendelse.getOpprettetDato());
        } else {
            throw new ApplicationException("Henvendelsen er ikke av typen XMlMeldingTilBruker: " + xmlMetadata);
        }
    }

    public static XMLHenvendelse createXMLHenvendelseMedMeldingTilBruker(SvarEllerReferat svarEllerReferat, XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withFnr(svarEllerReferat.fnr)
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withSporsmalsId(svarEllerReferat.sporsmalsId)
                                .withTemagruppe(svarEllerReferat.temagruppe)
                                .withKanal(svarEllerReferat.kanal)
                                .withFritekst(svarEllerReferat.fritekst)
                                .withNavident(svarEllerReferat.navIdent)
                ));
    }

    public static WSEndreOppgave tilWSEndreOppgave(WSOppgave wsOppgave) {
        return new WSEndreOppgave()
                .withOppgaveId(wsOppgave.getOppgaveId())
                .withAnsvarligId(wsOppgave.getAnsvarligId())
                .withBrukerId(wsOppgave.getGjelder().getBrukerId())
                .withDokumentId(wsOppgave.getDokumentId())
                .withKravId(wsOppgave.getKravId())
                .withAnsvarligEnhetId(wsOppgave.getAnsvarligEnhetId())

                .withFagomradeKode(wsOppgave.getFagomrade().getKode())
                .withOppgavetypeKode(wsOppgave.getOppgavetype().getKode())
                .withPrioritetKode(wsOppgave.getPrioritet().getKode())
                .withBrukertypeKode(wsOppgave.getGjelder().getBrukertypeKode())
                .withUnderkategoriKode(wsOppgave.getUnderkategori().getKode())

                .withAktivFra(wsOppgave.getAktivFra())
                .withBeskrivelse(wsOppgave.getBeskrivelse())
                .withVersjon(wsOppgave.getVersjon())
                .withSaksnummer(wsOppgave.getSaksnummer())
                .withLest(wsOppgave.isLest());
    }

}
