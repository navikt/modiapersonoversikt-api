package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.System.getProperties;
import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.ConfigUtil.isInMockMode;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.ConfigUtil.transformUrlStringToBoolean;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockSetupSingleton.mockSetup;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class ConfigUtilTest {

    public static final String KEY = "start.withmock";
    public static final String TILLATMOCKSETUP = "tillatmocksetup.url";
    public static final String JA_URL = "http://ja.no";

    @Before
    public void setUp() throws Exception {
        setProperty(TILLATMOCKSETUP, JA_URL);
    }

    @After
    public void tearDown() throws Exception {
        getProperties().remove(TILLATMOCKSETUP);
        mockSetup().clear();
    }

    @Test
    public void testTransformUrl_Nei_GirFalse() throws Exception {
        String url = "http://nei.nav.no";
        boolean urlOversatt = transformUrlStringToBoolean(url);
        assertThat(urlOversatt, is(equalTo(false)));
    }

    @Test
    public void testTransformUrl_HostInneholderJaIAnnetOrd_GirFalse() throws Exception {
        String url = "http://jajaja.nav.no";
        boolean urlOversatt = transformUrlStringToBoolean(url);
        assertThat(urlOversatt, is(equalTo(false)));
    }

    @Test
    public void testTransformUrl_MalformedUrl_GirFalse() throws Exception {
        String url = "nei.nav.no";
        boolean urlOversatt = transformUrlStringToBoolean(url);
        assertThat(urlOversatt, is(equalTo(false)));
    }

    @Test
    public void testTransformUrl_KunNei_GirFalse() throws Exception {
        String url = "nei";
        boolean urlOversatt = transformUrlStringToBoolean(url);
        assertThat(urlOversatt, is(equalTo(false)));
    }

    @Test
    public void testTransformUrl_UrlErIkkeSatt_GirFalse() throws Exception {
        boolean urlOversatt = transformUrlStringToBoolean(null);
        assertThat(urlOversatt, is(equalTo(false)));
    }

    @Test
    public void testTransformUrl_HostBegynnerMedJa_GirTrue() throws Exception {
        String url = "http://ja.nav.no";
        boolean urlOversatt = transformUrlStringToBoolean(url);
        assertThat(urlOversatt, is(equalTo(true)));
    }

    @Test
    public void testTransformUrl_HostBegynnerMedJaStoreBokstaver_GirTrue() throws Exception {
        String url = "http://Ja.nav.no";
        boolean urlOversatt = transformUrlStringToBoolean(url);
        assertThat(urlOversatt, is(equalTo(true)));
    }

    @Test
    public void testTransformUrl_HostInneholderJa_GirTrue() throws Exception {
        String url = "http://jabbadabba.ja.no";
        boolean urlOversatt = transformUrlStringToBoolean(url);
        assertThat(urlOversatt, is(equalTo(true)));
    }

    @Test
    public void isInMockMode_MockErTillatOgPropertyErYes_GirTrue() throws Exception {
        setProperty(TILLATMOCKSETUP, JA_URL);
        setProperty(KEY, "yes");
        assertThat(isInMockMode(KEY), is(equalTo(true)));
    }

    @Test
    public void isInMockMode_MockErTillatOgPropertyErNo_GirFalse() throws Exception {
        setProperty(TILLATMOCKSETUP, JA_URL);
        setProperty(KEY, "no");
        assertThat(isInMockMode(KEY), is(equalTo(false)));
    }

    @Test
    public void isInMockMode_MockErIkkeTillatOgPropertyErYes_GirFalse() throws Exception {
        setProperty(TILLATMOCKSETUP, "no");
        setProperty(KEY, "yes");
        assertThat(isInMockMode(KEY), is(equalTo(false)));
    }


}
