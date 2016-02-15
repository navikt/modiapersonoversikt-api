package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.detalj.Sak;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Journalpost;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaksServiceImpl implements SaksService {

    public SaksServiceImpl(PesysService pesysService) {
        this.pesysService = pesysService;
    }

    private PesysService pesysService;

    @Inject
    private GsakSakerService gsakSakerService;

    @Inject
    private InnsynJournalService innsynJournalService;


    public Optional<Stream<Journalpost>> hentJournalpostListe(String fnr) {
        Optional<Stream<Journalpost>> alleSaker = innsynJournalService.joarkSakhentTilgjengeligeJournalposter(hentAlleSaker(fnr));

        return alleSaker;
    }

    private List<Sak> hentAlleSaker(String fnr) {
        List<Sak> psakSaker = pesysService.hentSakstemaFraPesys(fnr);
        List<Sak> gsaker = gsakSakerService.hentSaker(fnr).get().collect(Collectors.toList());
        psakSaker.addAll(gsaker);
        return psakSaker;
    }
}
