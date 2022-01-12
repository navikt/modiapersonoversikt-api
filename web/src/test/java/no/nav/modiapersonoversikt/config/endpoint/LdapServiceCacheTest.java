package no.nav.modiapersonoversikt.config.endpoint;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LdapServiceCacheTest extends CacheTest {

    @Autowired
    private LDAPService ldapService;

    public LdapServiceCacheTest() {
        super("ldap");
    }

    @Test
    void cacheSetupMedRiktigKeyGenerator() {
        ldapService.hentSaksbehandler("Z999999");

        assertThat(getNativeCache().estimatedSize(), is(1L));
        assertThat(getKey(), is(generatedByMethodAwareKeyGenerator()));
    }

    @Test
    void cacheKeysSkalVareUnikeForUlikeMetoder() {
        verifyUniqueAndStableCacheKeys(
                () -> ldapService.hentSaksbehandler("Z999999"),
                () -> ldapService.hentRollerForVeileder("Z999999")
        );
    }
}
