package no.nav.sbl.dialogarena.mottaksbehandling.oppgave;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;
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
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.apache.commons.collections15.Transformer;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.ANSVARLIG_ENHET;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.ENDRET_AV_ENHET;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.FERDIGSTILT_AV_ENHET;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.OPPGAVEBESKRIVELSE;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.OPPGAVETYPEKODE;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.OPPRETTET_AV_ENHET;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.PRIORITETKODE;
import static no.nav.sbl.dialogarena.mottaksbehandling.oppgave.GsakVerdier.TESTFAMILIEN_AREMARK;
import static no.nav.sbl.dialogarena.mottaksbehandling.verktoy.Tid.gregorianNow;

//import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeSortering;

/**
 * Metoder for gsakOppgaveIntegration, integrasjon via besvarHenvendelse til GSAK
 */
public class OppgavesystemGsak implements Oppgavesystem {

    private no.nav.virksomhet.tjenester.oppgave.v2.Oppgave oppgaveWS;
    private Oppgavebehandling oppgavebehandlingWS;

    public OppgavesystemGsak(no.nav.virksomhet.tjenester.oppgave.v2.Oppgave oppgaveWS, Oppgavebehandling oppgavebehandlingWS) {
        this.oppgaveWS = oppgaveWS;
        this.oppgavebehandlingWS = oppgavebehandlingWS;
    }

    @Override
    public String lagOppgave(String behandlingsId, String fodselsnummer, Tema tema) {
        WSOpprettOppgave wsOpprettOppgave = new WSOpprettOppgave()
                .withAktivFra(gregorianNow())
                .withAnsvarligEnhetId(String.valueOf(ANSVARLIG_ENHET))
                .withBrukerId(fodselsnummer)
                .withBeskrivelse(OPPGAVEBESKRIVELSE)
                .withFagomradeKode(tema.fagomradekode)
//                .withHenvendelseId(behandlingsId) //Kommentert ut fordi GSAK tryner hvis flere oppgaver har samme henvendelseID
                .withLest(false)
                .withOppgavetypeKode(OPPGAVETYPEKODE)
                .withPrioritetKode(PRIORITETKODE);

        WSOpprettOppgaveResponse opprettOppgaveResponse = oppgavebehandlingWS.opprettOppgave(
                new WSOpprettOppgaveRequest().withOpprettOppgave(wsOpprettOppgave).withOpprettetAvEnhetId(OPPRETTET_AV_ENHET)
        );

        return opprettOppgaveResponse.getOppgaveId();
    }

    @Override
    public Record<Oppgave> hentOppgave(String oppgaveId) {
        WSHentOppgaveResponse hentOppgaveResponse;
        try {
            hentOppgaveResponse = oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId));
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
        WSOppgave oppgave = hentOppgaveResponse.getOppgave();
        return tilOppgave(oppgave);
    }


    private static Record<Oppgave> tilOppgave(WSOppgave oppg) {
        return new Record<Oppgave>()
            .with(Oppgave.id, oppg.getOppgaveId())
            .with(Oppgave.behandlingsid, oppg.getHenvendelseId())
            .with(Oppgave.fodselsnummer, oppg.getGjelder().getBrukerId())
            .with(Oppgave.saksbehandlerid, optional(oppg.getAnsvarligId()))
            .with(Oppgave.beskrivelse, optional(oppg.getBeskrivelse()))
            .with(Oppgave.ferdigstilt, oppg.getStatus().getKode().equals("F"))
            .with(Oppgave.tema, Tema.FAGOMRADEKODER.get(oppg.getFagomrade().getKode()))
            .with(Oppgave.aktivFra, oppg.getAktivFra())
            .with(Oppgave.versjon, oppg.getVersjon());
    }

    @Override
    public void fristill(String oppgaveId, String begrunnelse) {
        Record<Oppgave> oppgave = hentOppgave(oppgaveId);
        String beskrivelse = oppgave.get(Oppgave.beskrivelse).getOrElse(OPPGAVEBESKRIVELSE) + "\n" + begrunnelse + "\n";
        oppdaterOppgave(oppgave.with(Oppgave.beskrivelse, optional(beskrivelse)).with(Oppgave.saksbehandlerid, Optional.<String>none()));
    }

    @Override
    public void ferdigstill(String oppgaveId) {
        oppgavebehandlingWS.ferdigstillOppgaveBolk(
                new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId).withFerdigstiltAvEnhetId(FERDIGSTILT_AV_ENHET)
        );
    }

    @Override
    public Optional<Record<Oppgave>> plukkOppgave(Tema tema) {
        Optional<Record<Oppgave>> oppgave = alleredeTilordnetOppgave(saksbehandlerId());
        if (oppgave.isSome()) {
            return oppgave;
        }

        oppgave = ufordeltOppgave(tema);
        if (oppgave.isSome()) {
            Record<Oppgave> tilordnet = tilordnetTil(oppgave.get(), saksbehandlerId());
            oppdaterOppgave(tilordnet);
            return optional(tilordnet);
        } else {
            return none();
        }
    }

    private Record<Oppgave> tilordnetTil(Record<Oppgave> oppgave, String saksbehandler) {
        return oppgave.with(Oppgave.saksbehandlerid, optional(saksbehandler));
    }

    private Optional<Record<Oppgave>> ufordeltOppgave(Tema tema) {
        List<WSOppgave> oppgaver = oppgaveWS.finnOppgaveListe(new WSFinnOppgaveListeRequest()
                    .withFilter(fellesFilter().withMaxAntallSvar(100).withUfordelte(true))
                    .withSok(
                            new WSFinnOppgaveListeSok()
                                    .withAnsvarligEnhetId(String.valueOf(ANSVARLIG_ENHET))
                                    .withFagomradeKodeListe(tema.fagomradekode))
            ).getOppgaveListe();
        return on(oppgaver).map(TIL_OPPGAVE).head();
    }

    private Optional<Record<Oppgave>> alleredeTilordnetOppgave(String saksbehandlerid) {
        List<WSOppgave> oppgaver = oppgaveWS.finnOppgaveListe(new WSFinnOppgaveListeRequest()
                .withFilter(fellesFilter())
                .withSok(new WSFinnOppgaveListeSok().withAnsvarligId(saksbehandlerid))).getOppgaveListe();

        return on(oppgaver).map(TIL_OPPGAVE).head();
    }

    private WSFinnOppgaveListeFilter fellesFilter() {
        return new WSFinnOppgaveListeFilter()
                .withOppgavetypeKodeListe(OPPGAVETYPEKODE)
                .withOpprettetEnhetId(String.valueOf(OPPRETTET_AV_ENHET));
    }

    private String saksbehandlerId() {
        SubjectHandler subjectHandler = SubjectHandler.getSubjectHandler();
        return subjectHandler.getUid();
    }

    private static WSEndreOppgave tilEndreOppgave(Record<Oppgave> oppgave) {
        return new WSEndreOppgave()
                .withAktivFra(oppgave.get(Oppgave.aktivFra))
                .withBeskrivelse(oppgave.get(Oppgave.beskrivelse).getOrElse(""))
                .withFagomradeKode(oppgave.get(Oppgave.tema).fagomradekode)
                .withOppgaveId(oppgave.get(Oppgave.id))
                .withOppgavetypeKode(OPPGAVETYPEKODE) // TODO: Bør hentes fra oppgaven
                .withPrioritetKode(PRIORITETKODE) // TODO: Bør hentes fra oppgaven
                .withAnsvarligId(oppgave.get(Oppgave.saksbehandlerid).getOrElse(""))
                .withVersjon(oppgave.get(Oppgave.versjon));
    }

    private void oppdaterOppgave(Record<Oppgave> oppgave) {
        WSLagreOppgaveRequest wsLagreOppgaveRequest = new WSLagreOppgaveRequest()
                .withEndreOppgave(tilEndreOppgave(oppgave))
                .withEndretAvEnhetId(ENDRET_AV_ENHET);
        try {
            oppgavebehandlingWS.lagreOppgave(wsLagreOppgaveRequest);
        } catch (LagreOppgaveOppgaveIkkeFunnet e) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", e);
        }
    }

    private static final Transformer<WSOppgave, Record<Oppgave>> TIL_OPPGAVE = new Transformer<WSOppgave, Record<Oppgave>>() {
        @Override
        public Record<Oppgave> transform(WSOppgave wsOppgave) {
            return tilOppgave(wsOppgave);
        }
    };

    @Override
    public Pingable.Ping ping() {
        try {
            oppgaveWS.finnOppgaveListe(new WSFinnOppgaveListeRequest()
                    .withSok(new WSFinnOppgaveListeSok().withBrukerId(TESTFAMILIEN_AREMARK))
                    .withFilter(new WSFinnOppgaveListeFilter().withMaxAntallSvar(0))
            );
            return Pingable.Ping.lyktes("GSAK_OK");
        } catch (Exception e) {
            return Pingable.Ping.feilet("GSAK_ERROR", e);
        }
    }
}
