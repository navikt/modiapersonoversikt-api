package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.cache;

import net.sf.ehcache.CacheManager;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;

import java.lang.reflect.Method;

public class HenvendelsePortTypeCacheUtil {

    public static final String HENVENDELSE_PORT_TYPE_CACHE_NAME = "endpointCache";
    public static final String HENT_HENVENDELSE_LISTE_METODE_NAVN = "hentHenvendelseListe";

    public static void invaliderHentHenvendelseListeCacheElement(HenvendelsePortType henvendelsePortType, String fodselsnummer, String[] typer) {
        WSHentHenvendelseListeRequest parameter = new WSHentHenvendelseListeRequest()
                .withFodselsnummer(fodselsnummer)
                .withTyper(typer);

        Object cacheKey =  new AutentisertBrukerKeyGenerator().generate(henvendelsePortType, getHentHenvendelseMetode(), parameter);

        CacheManager.getCacheManager(CacheUtil.CACHE_MANAGER_NAME).getCache(HENVENDELSE_PORT_TYPE_CACHE_NAME).remove(cacheKey);
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
