package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.domain.Oppfolgingsinfo;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OppfolgingsinfoServiceImplTest {

    public static final boolean ER_UNDER_OPPFOLGING = true;
    private OppfolgingsinfoV1 oppfolgingsinfoV1Mock;
    private LDAPService ldapServiceMock;

    @BeforeEach
    void beforeEach() {
        setupMocks();
    }

    private void setupMocks() {
        oppfolgingsinfoV1Mock = mockOppfolginsinfoV1();
        ldapServiceMock = mockLdapService();
    }

    private OppfolgingsinfoV1 mockOppfolginsinfoV1() {
        OppfolgingsinfoV1 oppfolgingsinfoV1Mock = mock(OppfolgingsinfoV1.class);
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any())).thenReturn(new OppfolgingsstatusResponse()
                .withWsOppfolgingsdata(new WSOppfolgingsdata().withErUnderOppfolging(ER_UNDER_OPPFOLGING)));
        return oppfolgingsinfoV1Mock;
    }

    private LDAPService mockLdapService() {
        LDAPService ldapService = mock(LDAPService.class);
        when(ldapService.hentSaksbehandler(anyString())).thenReturn(new Saksbehandler("John", "Lennon", "z151444"));
        return ldapService;
    }

    @Test
    @DisplayName("Henter ut oppfølgingsflagget til bruker fra veilarboppfolging")
    void henterErUnderOppfolgingFraTjenesten() {
        OppfolgingsinfoService oppfolgingsinfoService = new OppfolgingsinfoServiceImpl(oppfolgingsinfoV1Mock, ldapServiceMock);

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoService.hentOppfolgingsinfo("10108000398");

        assertEquals(ER_UNDER_OPPFOLGING, oppfolgingsinfo.erUnderOppfolging);
    }

}