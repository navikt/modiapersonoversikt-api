package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.WSHenvendelseUtils.skillUtTraader;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class WSHenvendelseUtilsTest {

    @Test
    public void skillUtITraaderFungererPaaTomListe() {
        Map<String, List<WSHenvendelse>> traaderMap = skillUtTraader(Collections.<WSHenvendelse>emptyList());
        assertTrue(traaderMap.isEmpty());
    }

    @Test
    public void skillUtITraaderFungererPaaListeMedEttElement() {
        Map<String, List<WSHenvendelse>> traaderMap = skillUtTraader(asList(createWSHenvendelse("1")));
        Assert.assertThat(traaderMap.size(), equalTo(1));
    }
    @Test
    public void skalHenteRiktigAntallTraader() {
        List<WSHenvendelse> liste =
                asList(
                        createWSHenvendelse("1"),
                        createWSHenvendelse("1"),
                        createWSHenvendelse("2"),
                        createWSHenvendelse("3"),
                        createWSHenvendelse("3"),
                        createWSHenvendelse("3"));
        Map<String, List<WSHenvendelse>> traaderMap = skillUtTraader(liste);
        assertThat(traaderMap.size(), equalTo(3));
    }

    @Test
    public void traadSkalInnholdeKorrektAntallHenvendelser() {
        String traadId = "1";
        List<WSHenvendelse> liste =
                asList(
                        createWSHenvendelse(traadId),
                        createWSHenvendelse(traadId),
                        createWSHenvendelse(traadId),
                        createWSHenvendelse("100"),
                        createWSHenvendelse("100"),
                        createWSHenvendelse("123412"));
        Map<String, List<WSHenvendelse>> traaderMap = skillUtTraader(liste);
        Assert.assertThat(traaderMap.get(traadId).size(), equalTo(3));
    }

    @Test
    public void alleHenvendelserITraadSkalHaSammeTraadId() {
        String traadId = "1";
        List<WSHenvendelse> liste =
                asList(
                        createWSHenvendelse(traadId),
                        createWSHenvendelse(traadId),
                        createWSHenvendelse(traadId),
                        createWSHenvendelse("100"));
        Map<String, List<WSHenvendelse>> traaderMap = skillUtTraader(liste);
        List<WSHenvendelse> traad = traaderMap.get(traadId);
        assertThat(traad, everyItem(harTraadId(traadId)));
    }

    private Matcher<WSHenvendelse> harTraadId(final String traadId) {
        return new BaseMatcher<WSHenvendelse>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof WSHenvendelse) {
                    WSHenvendelse wsHenvendelse = (WSHenvendelse) o;
                    return traadId.equals(wsHenvendelse.getTraad());
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("TraadId Matcher for traadId = " + traadId);
            }
        };

    }

    private WSHenvendelse createWSHenvendelse(String traadId) {
        return new WSHenvendelse().withTraad(traadId);
    }
}
