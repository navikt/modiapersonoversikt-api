package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;


class GsakKodeverkTemaTest {
    @Test
    void shouldReadKodeverk() {
        List<GsakKodeTema.Tema> parse = GsakKodeverkTema.Parser.parse();
        assertThat(parse.size(), is(greaterThan(0)));
    }

    @Test
    void shouldSortKodeverk() {
        List<GsakKodeTema.Tema> temaer = GsakKodeverkTema.Parser.parse();

        List<String> tematekster = temaer.stream()
                .map(GsakKodeTema.TEKST)
                .collect(toList());

        List<String> sortertetematekster = temaer.stream()
                .map(GsakKodeTema.TEKST)
                .sorted()
                .collect(toList());

        assertThat(tematekster, contains(sortertetematekster.toArray()));
    }

    @Test
    public void underkategorierHvorErGyldigErLikFalseForGosysSkalIgnoreres() throws Exception {
        final List<GsakKodeTema.Tema> alleTema = GsakKodeverkTema.Parser.parse();
        final List<GsakKodeTema.Tema> etTema = alleTema.stream()
                .filter(tema -> tema.kode.equals("STO"))
                .collect(toList());

        assertThat(etTema.get(0).underkategorier.size(), is(1));
        assertThat(etTema.get(0).underkategorier.get(0).kode, is("AAP_STO"));
    }

    @Test
    public void underkategorierHvorErGyldigErTrueMenTOMErForbiForGosysSkalIgnoreres() throws Exception {
        final List<GsakKodeTema.Tema> alleTema = GsakKodeverkTema.Parser.parse();
        final List<GsakKodeTema.Tema> etTema = alleTema.stream()
                .filter(tema -> tema.kode.equals("MED"))
                .collect(toList());

        assertThat(etTema.get(0).underkategorier.size(), is(0));
    }

}
