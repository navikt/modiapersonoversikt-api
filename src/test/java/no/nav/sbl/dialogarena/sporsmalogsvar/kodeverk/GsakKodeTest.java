package no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GsakKodeTest {
    @Test
    public void shouldReadKodeverk() {
        List<GsakKode.Tema> parse = GsakKode.Parser.parse();
        assertThat(parse.size(), is(greaterThan(0)));
    }

}