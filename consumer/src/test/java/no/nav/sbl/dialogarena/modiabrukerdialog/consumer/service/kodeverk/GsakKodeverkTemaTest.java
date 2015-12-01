package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import org.apache.commons.collections15.Predicate;
import org.junit.Test;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GsakKodeverkTemaTest {
    @Test
    public void shouldReadKodeverk() {
        List<GsakKodeTema.Tema> parse = GsakKodeverkTema.Parser.parse();
        assertThat(parse.size(), is(greaterThan(0)));
    }

    @Test
    public void underkategorierHvorErGyldigErLikFalseForGosysSkalIgnoreres() throws Exception {
        final List<GsakKodeTema.Tema> alleTema = GsakKodeverkTema.Parser.parse();
        final List<GsakKodeTema.Tema> etTema = on(alleTema).filter(new Predicate<GsakKodeTema.Tema>() {
            @Override
            public boolean evaluate(final GsakKodeTema.Tema tema) {
                return tema.kode.equals("STO");
            }
        }).collect();

        assertThat(etTema.get(0).underkategorier.size(), is(1));
        assertThat(etTema.get(0).underkategorier.get(0).kode, is("AAP_STO"));
    }
}
