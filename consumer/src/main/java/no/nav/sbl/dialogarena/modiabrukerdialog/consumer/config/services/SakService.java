package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.HenvendelseUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSOppgave;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeFilter;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeSok;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeSortering;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSHentOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgave.v2.HentOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgave.v2.Oppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSEndreOppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSFerdigstillOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSLagreOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;

import javax.inject.Inject;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.HenvendelseUtils.createSporsmalFromHenvendelse;

public class SakService {

    public static final int ANSVARLIG_ENHET = 2820;
    public static final int OPPRETTET_AV_ENHET = 2820;
    public static final int ENDRET_AV_ENHET = 2820;
    public static final int FERDIGSTILT_AV_ENHET = 2820;
    public static final String OPPGAVETYPEKODE = "KONT_BRUK_GEN"; // Brukergenerert. Denne brukes lite og er dermed ganske safe

    private static final Map<String, String> FAGOMRADE;
    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", "BAR");
        tmp.put("FAMILIE_OG_BARN", "BID");
        tmp.put("HJELPEMIDLER", "HJE");
        tmp.put("OVRIGE_HENVENDELSER","GRA");
        FAGOMRADE = unmodifiableMap(tmp);
    }

    @Inject
    private Oppgavebehandling oppgavebehandlingWS;

    @Inject
    private Oppgave oppgaveWS;

    @Inject
    private HenvendelsePortType henvendelsePortType;

    @Inject
    protected SendHenvendelsePortType sendHenvendelsePortType;

    public void sendSvar(Svar svar) {
        XMLBehandlingsinformasjon info = HenvendelseUtils.createXMLBehandlingsinformasjon(svar);
        sendHenvendelsePortType.sendHenvendelse(new WSSendHenvendelseRequest().withType(SVAR.name()).withFodselsnummer(svar.fnr).withAny(info));
    }

    public void sendReferat(Referat referat) {
        XMLBehandlingsinformasjon info = HenvendelseUtils.createXMLBehandlingsinformasjon(referat);
        sendHenvendelsePortType.sendHenvendelse(new WSSendHenvendelseRequest().withType(REFERAT.name()).withFodselsnummer(referat.fnr).withAny(info));
    }

    public Sporsmal getSporsmalFromOppgaveId(String fnr, String oppgaveid) {
        List<Object> henvendelseListe =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(SPORSMAL.toString())).getAny();

        XMLBehandlingsinformasjon henvendelse;
        for (Object o : henvendelseListe) {
            henvendelse = (XMLBehandlingsinformasjon) o;
            XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLSporsmal && oppgaveid.equals(((XMLSporsmal) xmlMetadata).getOppgaveIdGsak())) {
                return createSporsmalFromHenvendelse(henvendelse);
            }
        }
        return null;
    }

    public Sporsmal getSporsmalOgTilordneIGsak(String sporsmalsId) {
        XMLBehandlingsinformasjon behandlingsinformasjon =
                (XMLBehandlingsinformasjon) henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(sporsmalsId)).getAny();
        XMLSporsmal xmlSporsmal = (XMLSporsmal) behandlingsinformasjon.getMetadataListe().getMetadata().get(0);
        tilordneOppgave(hentOppgaveFraGsak(xmlSporsmal.getOppgaveIdGsak()));
        return createSporsmalFromHenvendelse(behandlingsinformasjon);
    }

    public Optional<WSOppgave> plukkOppgaveFraGsak(String tema) {
        Optional<WSOppgave> oppgave = finnIkkeTilordnedeOppgaver(FAGOMRADE.get(tema));
        if (oppgave.isSome()) {
            WSOppgave tilordnet = tilordneOppgave(oppgave.get());
            return optional(tilordnet);
        } else {
            return none();
        }
    }

    public WSOppgave hentOppgaveFraGsak(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
    }

    public void ferdigstillOppgaveFraGsak(String oppgaveId) {
        oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId).withFerdigstiltAvEnhetId(FERDIGSTILT_AV_ENHET));
    }

    private WSOppgave tilordneOppgave(WSOppgave oppgave) {
        WSOppgave wsOppgave = oppgave.withAnsvarligId(getSubjectHandler().getUid());

        try {
            oppgavebehandlingWS.lagreOppgave(
                    new WSLagreOppgaveRequest()
                            .withEndreOppgave(new WSEndreOppgave()
                                    .withDokumentId(wsOppgave.getDokumentId())
                                    .withKravId(wsOppgave.getKravId())
                                    .withOppgaveId(wsOppgave.getOppgaveId())
                                    .withBrukerId(wsOppgave.getGjelder().getBrukerId())
                                    .withAnsvarligId(wsOppgave.getAnsvarligId())
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
                                    .withLest(wsOppgave.isLest()))
                            .withEndretAvEnhetId(ENDRET_AV_ENHET));
        } catch (LagreOppgaveOppgaveIkkeFunnet lagreOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", lagreOppgaveOppgaveIkkeFunnet);
        }

        return wsOppgave;
    }

    private Optional<WSOppgave> finnIkkeTilordnedeOppgaver(String tema) {
        return on(oppgaveWS.finnOppgaveListe(
                new WSFinnOppgaveListeRequest()
                        .withFilter(new WSFinnOppgaveListeFilter()
                                .withOpprettetEnhetId(valueOf(OPPRETTET_AV_ENHET))
                                .withOppgavetypeKodeListe(OPPGAVETYPEKODE)
                                .withMaxAntallSvar(1)
                                .withUfordelte(true))
                        .withSok(new WSFinnOppgaveListeSok()
                                .withAnsvarligEnhetId(valueOf(ANSVARLIG_ENHET))
                                .withFagomradeKodeListe(tema))
                        .withSorteringKode(new WSFinnOppgaveListeSortering().withSorteringKode("FRIST_DATO_STIG")))
                .getOppgaveListe())
                .head();
    }

}
