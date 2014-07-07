package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSOppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSEndreOppgave;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static org.joda.time.DateTime.now;

public class SakUtils {

    public static Sporsmal createSporsmalFromHenvendelse(Object henvendelsesObjekt) {
        XMLBehandlingsinformasjon info = (XMLBehandlingsinformasjon) henvendelsesObjekt;

        Sporsmal sporsmal = new Sporsmal(info.getBehandlingsId(), info.getOpprettetDato());

        XMLMetadata xmlMetadata = info.getMetadataListe().getMetadata().get(0);
        if(xmlMetadata instanceof XMLSporsmal){
            XMLSporsmal xmlSporsmal = (XMLSporsmal) xmlMetadata;
            sporsmal.temagruppe = xmlSporsmal.getTemagruppe();
            sporsmal.fritekst = xmlSporsmal.getFritekst();
            sporsmal.oppgaveId = xmlSporsmal.getOppgaveIdGsak();
            return sporsmal;
        } else {
            throw new ApplicationException("Henvendelsen er ikke av typen XMLSporsmal : " + xmlMetadata);
        }
    }

    public static XMLBehandlingsinformasjon createXMLBehandlingsinformasjon(Svar svar) {
        return new XMLBehandlingsinformasjon()
                .withHenvendelseType(SVAR.name())
                .withAktor(new XMLAktor().withFodselsnummer(svar.fnr).withNavIdent(svar.navIdent))
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLSvar()
                                .withSporsmalsId(svar.sporsmalsId)
                                .withTemagruppe(svar.temagruppe)
                                .withKanal(svar.kanal)
                                .withFritekst(svar.fritekst)
                ));
    }

    public static XMLBehandlingsinformasjon createXMLBehandlingsinformasjon(Referat referat) {
        return new XMLBehandlingsinformasjon()
                .withHenvendelseType(REFERAT.name())
                .withAktor(new XMLAktor().withFodselsnummer(referat.fnr).withNavIdent(referat.navIdent))
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLReferat().withTemagruppe(referat.temagruppe).withKanal(referat.kanal).withFritekst(referat.fritekst)));
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
