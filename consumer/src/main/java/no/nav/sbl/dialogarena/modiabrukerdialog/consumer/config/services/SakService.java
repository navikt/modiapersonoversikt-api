package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSOppgave;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeFilter;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeSok;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSHentOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSHentOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgave.v2.HentOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSEndreOppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSFerdigstillOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSLagreOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class SakService {

    public static final int ANSVARLIG_ENHET = 4112;
    public static final int OPPRETTET_AV_ENHET = 4112;
    public static final int ENDRET_AV_ENHET = 4112;
    public static final int FERDIGSTILT_AV_ENHET = 4112;
    public static final String OPPGAVETYPEKODE = "KONT_BRUK_GEN"; // Brukergenerert. Denne brukes lite og er dermed ganske safe
    public static final String PRIORITETKODE = "NORM_GEN"; // Normal prioritet - Generell


    @Inject
    private Oppgavebehandling oppgavebehandlingWS;

    @Inject
    private no.nav.virksomhet.tjenester.oppgave.v2.Oppgave oppgaveWS;

    @Inject
    protected SendHenvendelsePortType ws;

    public Melding getSakFromHenvendelse(String sporsmalsId) {
        return getMelding();
    }

    public Oppgave hentOppgaveFraGsak(String oppgaveId) {
        WSHentOppgaveResponse wsHentOppgaveResponse;
        try {
            wsHentOppgaveResponse = oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId));
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
        return tilOppgave(wsHentOppgaveResponse.getOppgave());
    }

    public Optional<Oppgave> plukkOppgaveFraGsak(String tema) {
        Optional<Oppgave> oppgave = finnIkkeTilordnedeOppgaver(tema);
        if (oppgave.isSome()) {
            Oppgave tilordnet = oppgave.get().withSaksbehandlerid(optional(getSubjectHandler().getUid()));
            oppdaterOppgave(tilordnet);
            return optional(tilordnet);
        } else {
            return none();
        }
    }

    public void ferdigstillOppgaveFraGsak(String oppgaveId) {
        oppgavebehandlingWS.ferdigstillOppgaveBolk(
                new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId).withFerdigstiltAvEnhetId(FERDIGSTILT_AV_ENHET));
    }

    private Melding getMelding() {
        Melding melding = new Melding("id", Meldingstype.SPORSMAL, DateTime.now());
        melding.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, " +
                "sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl " +
                "ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate " +
                "velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
        melding.tema = "HJELPEMIDLER";
        return melding;
    }

    private Optional<Oppgave> finnIkkeTilordnedeOppgaver(String tema) {
        List<WSOppgave> oppgaveListe = oppgaveWS.finnOppgaveListe(new WSFinnOppgaveListeRequest()
                .withFilter(fellesFilter().withMaxAntallSvar(1).withUfordelte(true))
                .withSok(
                        new WSFinnOppgaveListeSok()
                                .withAnsvarligEnhetId(String.valueOf(ANSVARLIG_ENHET))
                                .withFagomradeKodeListe(tema))).getOppgaveListe();

        return on(oppgaveListe).map(TIL_OPPGAVE).head();
    }


    private void oppdaterOppgave(Oppgave oppgave) {
        try {
            oppgavebehandlingWS.lagreOppgave(new WSLagreOppgaveRequest()
                    .withEndreOppgave(tilEndreOppgave(oppgave))
                    .withEndretAvEnhetId(ENDRET_AV_ENHET));
        } catch (LagreOppgaveOppgaveIkkeFunnet e) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", e);
        }
    }

    private static Oppgave tilOppgave(WSOppgave wsOppgave) {
        return new Oppgave()
                .withId(wsOppgave.getOppgaveId())
                .withBehandlingsid(wsOppgave.getHenvendelseId())
                .withFodselsnummer(wsOppgave.getGjelder().getBrukerId())
                .withSaksbehandlerid(optional(wsOppgave.getAnsvarligId()))
                .withBeskrivelse(optional(wsOppgave.getBeskrivelse()))
                .withFerdigstilt(wsOppgave.getStatus().getKode().equals("F"))
                .withTema(wsOppgave.getFagomrade().getKode())
                .withAktivFra(wsOppgave.getAktivFra())
                .withVersjon(wsOppgave.getVersjon());
    }

    private static WSEndreOppgave tilEndreOppgave(Oppgave oppgave) {
        return new WSEndreOppgave()
                .withAktivFra(oppgave.getAktivFra())
                .withBeskrivelse(oppgave.getBeskrivelse().getOrElse(""))
                .withFagomradeKode(oppgave.getTema())
                .withOppgaveId(oppgave.getId())
                .withOppgavetypeKode(OPPGAVETYPEKODE)
                .withPrioritetKode(PRIORITETKODE)
                .withAnsvarligId(oppgave.getSaksbehandlerid().getOrElse(""))
                .withVersjon(oppgave.getVersjon());
    }

    private WSFinnOppgaveListeFilter fellesFilter() {
        return new WSFinnOppgaveListeFilter()
                .withOppgavetypeKodeListe(OPPGAVETYPEKODE)
                .withOpprettetEnhetId(String.valueOf(OPPRETTET_AV_ENHET));
    }

    private static final Transformer<WSOppgave, Oppgave> TIL_OPPGAVE = new Transformer<WSOppgave, Oppgave>() {
        @Override
        public Oppgave transform(WSOppgave wsOppgave) {
            return tilOppgave(wsOppgave);
        }
    };

}
