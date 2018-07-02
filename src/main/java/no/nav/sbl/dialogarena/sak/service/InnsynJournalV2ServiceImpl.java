package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.InnsynJournalV2Service;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostRequest;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostResponse;

import javax.inject.Inject;

public class InnsynJournalV2ServiceImpl implements InnsynJournalV2Service {


    @Inject
    private InnsynJournalV2 innsynJournalV2;

    @Override
    public ResultatWrapper<String> identifiserJournalpost(String fnr) {
        IdentifiserJournalpostResponse response;
        try {
            response = innsynJournalV2.identifiserJournalpost(new IdentifiserJournalpostRequest());
        }catch (Exception e){

        }
        return new ResultatWrapper<>("hei");
    }
}
