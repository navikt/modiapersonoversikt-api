package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.context.ApplicationContextProvider;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;

import java.util.concurrent.Callable;
import java.util.stream.Stream;


public class PesysCallable implements Callable<Stream<Sak>> {

    private String uId;
    private String sessionId;

    public PesysCallable(String uId, String sessionId) {
        this.uId = uId;
        this.sessionId = sessionId;
    }

    @Override
    public Stream<Sak> call() {
        PesysService pesysService = ApplicationContextProvider.getContext().getBean("pesysService", PesysService.class);
        return pesysService.hentSakstemaFraPesys(uId, sessionId).stream();
    }
}
