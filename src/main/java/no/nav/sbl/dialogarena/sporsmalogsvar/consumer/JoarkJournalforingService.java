package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.*;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.*;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingInngaaende.lagJournalforingInngaaende;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingNotat.lagJournalforingNotat;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingUtgaaende.lagJournalforingUtgaaende;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.*;

public class JoarkJournalforingService {

    public static final String MODIA_SYSTEM_ID = "BD06";
    private static final List<Meldingstype> INNGAENDE = asList(SVAR_SBL_INNGAAENDE, SPORSMAL_SKRIFTLIG);
    private static final List<Meldingstype> UTGAENDE = asList(SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SPORSMAL_MODIA_UTGAAENDE);
    private static final List<Meldingstype> NOTAT = asList(SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON);

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
            journalpostIdEldsteMelding = behandleJournalforing(eldsteMelding, sak, Optional.<String>none());
            henvendelseBehandlingService.oppdaterJournalfortInformasjonIHenvendelse(sak, journalpostIdEldsteMelding, eldsteMelding);
        } else {
            journalpostIdEldsteMelding = eldsteMelding.journalfortSaksId;
        }
        for (MeldingVM meldingVM : valgtTraad.getMeldinger().subList(0, valgtTraad.getMeldinger().size() - 1)) {
            Melding melding = meldingVM.melding;
            if (melding.journalfortDato == null) {
                String journalpostId = behandleJournalforing(melding, sak, optional(journalpostIdEldsteMelding));
                henvendelseBehandlingService.oppdaterJournalfortInformasjonIHenvendelse(sak, journalpostId, melding);
            }
        }
    }

    private String behandleJournalforing(Melding melding, Sak sak, Optional<String> journalpostIdEldsteMelding) {
        if (INNGAENDE.contains(melding.meldingstype)) {
            return behandleJournalforingInngaende(melding, sak, journalpostIdEldsteMelding);
        } else if (UTGAENDE.contains(melding.meldingstype)) {
            return (behandleJournalforingUtgaende(melding, sak, journalpostIdEldsteMelding));
        } else if (NOTAT.contains(melding.meldingstype)) {
            return (behandleJournalforingNotat(melding, sak, journalpostIdEldsteMelding));
        }

        throw new RuntimeException("Meldingen har ingen kjent meldingstype: " + melding.meldingstype);
    }

    private String behandleJournalforingInngaende(Melding melding, Sak sak, Optional<String> journalpostIdEldsteMelding) {
        JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest = new JournalfoerInngaaendeHenvendelseRequest();

        journalfoerInngaaendeHenvendelseRequest.setPersonEtternavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setPersonFornavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerInngaaendeHenvendelseRequest.setJournalpost(lagJournalforingInngaaende(
                journalpostIdEldsteMelding, sak, melding, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()));

        JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponse = behandleJournalV2.journalfoerInngaaendeHenvendelse(journalfoerInngaaendeHenvendelseRequest);
        return journalfoerInngaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalforingUtgaende(Melding melding, Sak sak, Optional<String> journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest = new JournalfoerUtgaaendeHenvendelseRequest();

        journalfoerUtgaaendeHenvendelseRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerUtgaaendeHenvendelseRequest.setJournalpost(lagJournalforingUtgaaende(
                journalfortPostIdForTilhorendeSporsmal, sak, melding, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()));

        JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponse = behandleJournalV2.journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequest);
        return journalfoerUtgaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalforingNotat(Melding melding, Sak sak, Optional<String> journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerNotatRequest journalfoerNotatRequest = new JournalfoerNotatRequest();

        journalfoerNotatRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerNotatRequest.setJournalpost(lagJournalforingNotat(
                journalfortPostIdForTilhorendeSporsmal, sak, melding, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()));

        JournalfoerNotatResponse journalfoerNotatResponse = behandleJournalV2.journalfoerNotat(journalfoerNotatRequest);
        return journalfoerNotatResponse.getJournalpostId();
    }

}
