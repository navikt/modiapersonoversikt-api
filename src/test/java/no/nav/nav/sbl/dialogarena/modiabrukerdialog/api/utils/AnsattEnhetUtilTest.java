package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AnsattEnhetUtilTest {

    @Test
    public void skalKunHaTilgangTilEgetKontorVedNavMidtbyen() {
        assertThat(AnsattEnhetUtil.hentEnheterForValgtEnhet("1603")).containsExactly("1603");
    }

    @Test
    public void skalHaTilgangTilFlereAvdelingskontorVedNavVaernes() {
        Set<String> enheter = AnsattEnhetUtil.hentEnheterForValgtEnhet("1783");
        assertThat(enheter).containsOnly("1783", "1664", "1665", "1711", "1714", "1717");
    }

    @Test
    public void skalHaTilgangTilEkstraEnheterINordTroendelag() throws Exception {
        Set<String> enheter = AnsattEnhetUtil.hentEkstraEnheterForFylke("1718");
        assertThat(enheter).containsOnly("1664", "1665", "1711", "1714", "1717");
    }

    @Test
    public void skalIkkeHaTilgangTilEkstraEnheterISoerTroendelag() throws Exception {
        Set<String> enheter = AnsattEnhetUtil.hentEkstraEnheterForFylke("1603");
        assertThat(enheter).isEmpty();
    }
}