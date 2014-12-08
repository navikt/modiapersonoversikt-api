package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;

import java.lang.reflect.Method;

public class GsakKeyGenerator extends AutentisertBrukerKeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        WSFinnGenerellSakListeRequest req = (WSFinnGenerellSakListeRequest) params[0];
        String id = req.getBrukerId();
        String fag = req.getFagomradeKodeListe().toString();
        return super.generate(target, method, id + ":" + fag);
    }
}
