package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.cache;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.springframework.cache.CacheManager;

import java.lang.reflect.Method;
import java.util.List;

public class HenvendelsePortTypeCacheUtil {

    public static final String HENVENDELSE_PORT_TYPE_CACHE_NAME = "endpointCache";
    public static final String HENT_HENVENDELSE_LISTE_METODE_NAVN = "hentHenvendelseListe";

    public static void invaliderHentHenvendelseListeCacheElement(CacheManager cacheManager, HenvendelsePortType henvendelsePortType, String fodselsnummer, List<String> typer) {
        WSHentHenvendelseListeRequest parameter = new WSHentHenvendelseListeRequest()
                .withFodselsnummer(fodselsnummer)
                .withTyper(typer);

        Object cacheKey =  new AutentisertBrukerKeyGenerator().generate(henvendelsePortType, getHentHenvendelseMetode(), parameter);

        cacheManager.getCache(HENVENDELSE_PORT_TYPE_CACHE_NAME).evict(cacheKey);
    }

    private static Method getHentHenvendelseMetode() {
        Class[] parameterTypes = getParameterTyper();
        try {
            return HenvendelsePortType.class.getMethod(HENT_HENVENDELSE_LISTE_METODE_NAVN, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Fant ikke metode der cachen skal invalideres: " + HENT_HENVENDELSE_LISTE_METODE_NAVN, e);
        }
    }

    private static Class[] getParameterTyper() {
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = WSHentHenvendelseListeRequest.class;
        return parameterTypes;
    }

}
