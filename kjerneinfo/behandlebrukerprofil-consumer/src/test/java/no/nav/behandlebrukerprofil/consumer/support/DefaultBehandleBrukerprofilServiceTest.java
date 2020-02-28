package no.nav.behandlebrukerprofil.consumer.support;

import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.behandlebrukerprofil.consumer.support.mapping.BehandleBrukerprofilMapper;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.BehandleBrukerprofilV2;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.*;

public class DefaultBehandleBrukerprofilServiceTest {

    private static final String SUBJECT_ID = "z999999";
    private static final String BRUKER_IDENT = "1010800398";

    private BehandleBrukerprofilMapper mapper;
    private BehandleBrukerprofilV2 wsService;
    private CacheManager cacheManager;
    private Cache cache;

    @Before
    public void before() {
        mapper = BehandleBrukerprofilMapper.getInstance();
        wsService = mock(BehandleBrukerprofilV2.class);
        cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
    }

    @Test
    public void nyttSokSletterCache() {
        DefaultBehandleBrukerprofilService service = new DefaultBehandleBrukerprofilService(wsService, null, mapper, cacheManager);
        BehandleBrukerprofilRequest request = new BehandleBrukerprofilRequest(lagBruker());

        SubjectHandler.withSubject(
                new Subject(SUBJECT_ID, IdentType.InternBruker, SsoToken.oidcToken("test", emptyMap())),
                () -> service.oppdaterKontaktinformasjonOgPreferanser(request)
        );

        verify(cache, times(1)).evict(SUBJECT_ID + BRUKER_IDENT + "true");
        verify(cache, times(1)).evict(SUBJECT_ID + BRUKER_IDENT + "false");
    }

    private Bruker lagBruker() {
        Bruker bruker = new Bruker();
        bruker.setIdent(BRUKER_IDENT);
        return bruker;
    }
}