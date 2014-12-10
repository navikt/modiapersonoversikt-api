package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingInngaaende;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingNotat;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingUtgaaende;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.*;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.optional;

public class JoarkJournalforingService {

    public static final String MODIA_SYSTEM_ID = "BD06";
    private static final List<Meldingstype> INNGANEDE = asList(Meldingstype.SVAR_SBL_INNGAAENDE, Meldingstype.SPORSMAL_SKRIFTLIG);
    private static final List<Meldingstype> UTGAENDE = asList(Meldingstype.SVAR_SKRIFTLIG, Meldingstype.SVAR_OPPMOTE, Meldingstype.SVAR_TELEFON, Meldingstype.SPORSMAL_MODIA_UTGAAENDE);
    private static final List<Meldingstype> NOTAT = asList(Meldingstype.SAMTALEREFERAT_OPPMOTE, Meldingstype.SAMTALEREFERAT_TELEFON);

    @Inject
    private BehandleJournalV2 behandleJournalV2;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    public void journalforTraad(TraadVM valgtTraad, Sak sak) {
        Melding eldsteMelding = valgtTraad.getEldsteMelding().melding;
        String journalpostIdEldsteMelding;
        if (eldsteMelding.journalfortDato == null) {
            journalpostIdEldsteMelding = behandleJournalforing(eldsteMelding, sak, null);
            henvendelseBehandlingService.oppdaterJournalfortInformasjonIHenvendelse(sak, journalpostIdEldsteMelding, eldsteMelding);
        } else {
            journalpostIdEldsteMelding = eldsteMelding.journalfortSaksId;
        }
        for (MeldingVM meldingVM : valgtTraad.getMeldinger().subList(0, valgtTraad.getMeldinger().size() - 1)) {
            Melding melding = meldingVM.melding;
            if (melding.journalfortDato == null) {
                String journalpostId = behandleJournalforing(melding, sak, journalpostIdEldsteMelding);
                henvendelseBehandlingService.oppdaterJournalfortInformasjonIHenvendelse(sak, journalpostId, melding);
            }
        }
    }

    private String behandleJournalforing(Melding melding, Sak sak, String journalpostIdEldsteMelding) {
        if (INNGANEDE.contains(melding.meldingstype)) {
            return behandleJournalSporsmal(melding, sak);
        } else if (UTGAENDE.contains(melding.meldingstype)) {
            return (behandleJournalSvar(melding, sak, journalpostIdEldsteMelding));
        } else if (NOTAT.contains(melding.meldingstype)) {
            return (behandleJournalSamtalereferat(melding, sak, optional(journalpostIdEldsteMelding)));
        }

        throw new RuntimeException("Meldingen har ingen kjent meldingstype: " + melding.meldingstype);
    }

    private String behandleJournalSporsmal(Melding melding, Sak sak) {
        JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest = new JournalfoerInngaaendeHenvendelseRequest();

        journalfoerInngaaendeHenvendelseRequest.setPersonEtternavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setPersonFornavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerInngaaendeHenvendelseRequest.setJournalpost(JournalforingInngaaende.lagJournalforingSporsmal(
                sak, melding, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()));

        JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponse = behandleJournalV2.journalfoerInngaaendeHenvendelse(journalfoerInngaaendeHenvendelseRequest);
        return journalfoerInngaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSvar(Melding melding, Sak sak, String journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest = new JournalfoerUtgaaendeHenvendelseRequest();

        journalfoerUtgaaendeHenvendelseRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerUtgaaendeHenvendelseRequest.setJournalpost(JournalforingUtgaaende.lagJournalforingSvar(
                journalfortPostIdForTilhorendeSporsmal, sak, melding, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()));

        JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponse = behandleJournalV2.journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequest);
        return journalfoerUtgaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSamtalereferat(Melding melding, Sak sak, Optional<String> journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerNotatRequest journalfoerNotatRequest = new JournalfoerNotatRequest();

        journalfoerNotatRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerNotatRequest.setJournalpost(JournalforingNotat.lagJournalforingNotat(
                journalfortPostIdForTilhorendeSporsmal, sak, melding, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()));

        JournalfoerNotatResponse journalfoerNotatResponse = behandleJournalV2.journalfoerNotat(journalfoerNotatRequest);
        return journalfoerNotatResponse.getJournalpostId();
    }

}
