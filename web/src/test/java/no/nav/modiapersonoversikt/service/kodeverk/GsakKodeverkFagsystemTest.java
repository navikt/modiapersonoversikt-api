package no.nav.modiapersonoversikt.service.kodeverk;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GsakKodeverkFagsystemTest {
    @Test
    public void shouldReadKodeverk() {
        Map<String, String> map = GsakKodeverkFagsystem.Parser.parse();
        assertThat(map.size(), is(greaterThan(0)));
    }
}
