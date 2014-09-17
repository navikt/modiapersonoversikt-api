package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingInngaaende;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingNotat;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingUtgaaende;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseResponse;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL_SKRIFTLIG;

public class JoarkJournalforingService {

    public static final String MODIA_SYSTEM_ID = "BD06";

    @Inject
    private BehandleJournalV2 behandleJournalV2;
    @Inject
    private ValgtEnhetService valgtEnhetService;
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
        if (melding.meldingstype.equals(SPORSMAL_SKRIFTLIG)) {
            return behandleJournalSporsmal(melding, sak);
        } else if (SVAR.contains(melding.meldingstype)) {
            return (behandleJournalSvar(melding, sak, journalpostIdEldsteMelding));
        } else {
            return (behandleJournalSamtalereferat(melding, sak, optional(journalpostIdEldsteMelding)));
        }
    }

    private String behandleJournalSporsmal(Melding melding, Sak sak) {
        JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest = new JournalfoerInngaaendeHenvendelseRequest();

        journalfoerInngaaendeHenvendelseRequest.setPersonEtternavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setPersonFornavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerInngaaendeHenvendelseRequest.setJournalpost(JournalforingInngaaende.lagJournalforingSporsmal(sak, melding, valgtEnhetService.getEnhetId()));

        JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponse = behandleJournalV2.journalfoerInngaaendeHenvendelse(journalfoerInngaaendeHenvendelseRequest);
        return journalfoerInngaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSvar(Melding melding, Sak sak, String journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest = new JournalfoerUtgaaendeHenvendelseRequest();

        journalfoerUtgaaendeHenvendelseRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerUtgaaendeHenvendelseRequest.setJournalpost(JournalforingUtgaaende.lagJournalforingSvar(journalfortPostIdForTilhorendeSporsmal, sak, melding, valgtEnhetService.getEnhetId()));

        JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponse = behandleJournalV2.journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequest);
        return journalfoerUtgaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSamtalereferat(Melding melding, Sak sak, Optional<String> journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerNotatRequest journalfoerNotatRequest = new JournalfoerNotatRequest();

        journalfoerNotatRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setApplikasjonsID(MODIA_SYSTEM_ID);
        journalfoerNotatRequest.setJournalpost(JournalforingNotat.lagJournalforingNotat(journalfortPostIdForTilhorendeSporsmal, sak, melding, valgtEnhetService.getEnhetId()));

        JournalfoerNotatResponse journalfoerNotatResponse = behandleJournalV2.journalfoerNotat(journalfoerNotatRequest);
        return journalfoerNotatResponse.getJournalpostId();
    }

}
