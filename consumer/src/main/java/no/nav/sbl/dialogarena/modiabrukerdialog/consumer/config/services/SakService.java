package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSFagomrade;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeFilter;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeSok;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeSortering;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFerdigstillOppgaveBolkRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableMap;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils.createSporsmalFromHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils.createSvarFromHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils.createXMLBehandlingsinformasjon;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils.tilWSEndreOppgave;

public class SakService {

    public static final int ANSVARLIG_ENHET = 4112;
    public static final int OPPRETTET_AV_ENHET = 4112;
    public static final int ENDRET_AV_ENHET = 4112;
    public static final int FERDIGSTILT_AV_ENHET = 4112;
    public static final String OPPGAVETYPEKODE = "KONT_BRUK_GEN"; // Brukergenerert. Denne brukes lite og er dermed ganske safe

    private static final Map<String, String> TEMAGRUPPE;
    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", "BAR");
        tmp.put("FAMILIE_OG_BARN", "BID");
        tmp.put("HJELPEMIDLER", "HJE");
        tmp.put("OVRIGE_HENVENDELSER","GRA");
        TEMAGRUPPE = unmodifiableMap(tmp);
    }

    @Inject
    private OppgavebehandlingV3 oppgavebehandlingWS;

    @Inject
    private OppgaveV3 oppgaveWS;

    @Inject
    private HenvendelsePortType henvendelsePortType;

    @Inject
    protected SendUtHenvendelsePortType sendUtHenvendelsePortType;

    public void sendSvar(Svar svar) {
        XMLBehandlingsinformasjon info = createXMLBehandlingsinformasjon(svar);
        sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest().withType(SVAR.name()).withFodselsnummer(svar.fnr).withAny(info));
    }

    public void sendReferat(Referat referat) {
        XMLBehandlingsinformasjon info = createXMLBehandlingsinformasjon(referat);
        sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest().withType(REFERAT.name()).withFodselsnummer(referat.fnr).withAny(info));
    }

    public Sporsmal getSporsmalFromOppgaveId(String fnr, String oppgaveId) {
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withTyper(SPORSMAL.name()).withFodselsnummer(fnr)).getAny();

        XMLBehandlingsinformasjon henvendelse;
        for (Object o : henvendelseliste) {
            henvendelse = (XMLBehandlingsinformasjon) o;
            XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLSporsmal && oppgaveId.equals(((XMLSporsmal) xmlMetadata).getOppgaveIdGsak())) {
                return createSporsmalFromHenvendelse(henvendelse);
            }
        }
        return null;
    }

    public List<Svar> getSvarTilSporsmal(String fnr, String sporsmalId) {
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withTyper(SVAR.name()).withFodselsnummer(fnr)).getAny();

        List<Svar> svarliste = new ArrayList<>();

        XMLBehandlingsinformasjon henvendelse;
        for (Object o : henvendelseliste) {
            henvendelse = (XMLBehandlingsinformasjon) o;
            XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLSvar && ((XMLSvar) xmlMetadata).getSporsmalsId().equals(sporsmalId)) {
                svarliste.add(createSvarFromHenvendelse(henvendelse));
            }
        }
        return svarliste;
    }

    public Sporsmal getSporsmal(String sporsmalId) {
        XMLBehandlingsinformasjon behandlingsinformasjon =
                (XMLBehandlingsinformasjon) henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(sporsmalId)).getAny();
        return createSporsmalFromHenvendelse(behandlingsinformasjon);
    }

    public void tilordneOppgaveIGsak(String oppgaveId) {
        tilordneOppgave(hentOppgaveFraGsak(oppgaveId));
    }

    public Optional<WSOppgave> plukkOppgaveFraGsak(String temagruppe) {
        Optional<WSOppgave> oppgave = finnIkkeTilordnedeOppgaver(TEMAGRUPPE.get(temagruppe));
        if (oppgave.isSome()) {
            WSOppgave tilordnet = tilordneOppgave(oppgave.get());
            return optional(tilordnet);
        } else {
            return none();
        }
    }

    public void ferdigstillOppgaveIGsak(Optional<String> oppgaveId) {
        if (oppgaveId.isSome()) {
            oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId.get()).withFerdigstiltAvEnhetId(FERDIGSTILT_AV_ENHET));
        }
    }

    public void leggTilbakeOppgaveIGsak(Optional<String> oppgaveId, String beskrivelse, String temagruppe) {
        if (oppgaveId.isSome()) {
            WSOppgave wsOppgave = hentOppgaveFraGsak(oppgaveId.get());
            wsOppgave.withAnsvarligId("");
            wsOppgave.withBeskrivelse(beskrivelse);
            if (temagruppe != null) {
                wsOppgave.withFagomrade(new WSFagomrade().withKode(TEMAGRUPPE.get(temagruppe)));
            }
            lagreOppgaveIGsak(wsOppgave);
        }
    }

    private WSOppgave hentOppgaveFraGsak(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
    }

    private WSOppgave tilordneOppgave(WSOppgave oppgave) {
        WSOppgave wsOppgave = oppgave.withAnsvarligId(getSubjectHandler().getUid());
        lagreOppgaveIGsak(wsOppgave);
        return wsOppgave;
    }

    private void lagreOppgaveIGsak(WSOppgave wsOppgave) {
        try {
            oppgavebehandlingWS.lagreOppgave(
                    new WSLagreOppgaveRequest()
                            .withEndreOppgave(tilWSEndreOppgave(wsOppgave))
                            .withEndretAvEnhetId(ENDRET_AV_ENHET));
        } catch (LagreOppgaveOppgaveIkkeFunnet lagreOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", lagreOppgaveOppgaveIkkeFunnet);
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            //TODO: Hva skal skje ved optimistisk låsing. Noen andre har låst filen.
        }
    }

    private Optional<WSOppgave> finnIkkeTilordnedeOppgaver(String temagruppe) {
        return on(oppgaveWS.finnOppgaveListe(
                new WSFinnOppgaveListeRequest()
                        .withFilter(new WSFinnOppgaveListeFilter()
                                .withOpprettetEnhetId(valueOf(OPPRETTET_AV_ENHET))
                                .withOppgavetypeKodeListe(OPPGAVETYPEKODE)
                                .withMaxAntallSvar(1)
                                .withUfordelte(true))
                        .withSok(new WSFinnOppgaveListeSok()
                                .withAnsvarligEnhetId(valueOf(ANSVARLIG_ENHET))
                                .withFagomradeKodeListe(temagruppe))
                        .withSorteringKode(new WSFinnOppgaveListeSortering()
                                .withSorteringKode("STIGENDE")
                                .withSorteringselementKode("OPPRETTET_DATO")))
                .getOppgaveListe())
                .head();
    }

}
