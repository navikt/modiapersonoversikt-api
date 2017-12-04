package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.stream.Collectors;


public class OrganisasjonEnhetKontantinformasjonKeyGenerator extends SimpleKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params == null || params.length == 0 || ! (params[0] instanceof HentKontaktinformasjonForEnhetBolkRequest)) {
            throw new IllegalArgumentException();
        }

        HentKontaktinformasjonForEnhetBolkRequest request = (HentKontaktinformasjonForEnhetBolkRequest) params[0];

        return super.generate(target, method, lagKey(request));    }

    private String lagKey(HentKontaktinformasjonForEnhetBolkRequest request) {
        return request.getEnhetIdListe().stream()
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.joining(","));
    }

}
