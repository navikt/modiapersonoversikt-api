package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GsakKodeverkFagsystemTest {
    @Test
    public void shouldReadKodeverk() {
        Map<String, String> map = GsakKodeverkFagsystem.Parser.parse();
        assertThat(map.size(), is(greaterThan(0)));
    }
}
