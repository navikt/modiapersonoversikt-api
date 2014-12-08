package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.GsakKodeTema;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GsakKodeverkTemaTest {
    @Test
    public void shouldReadKodeverk() {
        List<GsakKodeTema.Tema> parse = GsakKodeverkTema.Parser.parse();
        assertThat(parse.size(), is(greaterThan(0)));
    }
}
