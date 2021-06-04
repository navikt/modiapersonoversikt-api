package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.WSHentKontaktinformasjonForEnhetBolkRequest;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.stream.Collectors;


public class OrganisasjonenhetKontaktinformasjonKeyGenerator extends SimpleKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params == null || params.length == 0 || ! (params[0] instanceof WSHentKontaktinformasjonForEnhetBolkRequest)) {
            throw new IllegalArgumentException();
        }

        WSHentKontaktinformasjonForEnhetBolkRequest request = (WSHentKontaktinformasjonForEnhetBolkRequest) params[0];

        return super.generate(target, method, lagKey(request));
    }

    private String lagKey(WSHentKontaktinformasjonForEnhetBolkRequest request) {
        return request.getEnhetIdListe().stream()
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.joining(","));
    }

}
