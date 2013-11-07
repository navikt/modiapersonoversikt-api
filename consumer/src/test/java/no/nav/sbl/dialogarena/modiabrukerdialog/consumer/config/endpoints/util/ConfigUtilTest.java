package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;

import org.junit.Test;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.ConfigUtil.transformUrlStringToBoolean;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class ConfigUtilTest {
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
}
