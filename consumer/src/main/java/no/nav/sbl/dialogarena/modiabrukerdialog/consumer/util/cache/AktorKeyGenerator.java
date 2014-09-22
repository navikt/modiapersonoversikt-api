package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;

import java.lang.reflect.Method;

public class AktorKeyGenerator extends AutentisertBrukerKeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        HentAktoerIdForIdentRequest request = (HentAktoerIdForIdentRequest) params[0];
        return super.generate(target, method, request.getIdent());
    }
}
