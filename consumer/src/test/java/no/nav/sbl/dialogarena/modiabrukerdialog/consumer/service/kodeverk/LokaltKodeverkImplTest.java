package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import org.apache.commons.collections15.Transformer;
import org.junit.Test;

import java.util.List;
import java.util.Map.Entry;

import static no.nav.modig.lang.collections.IterUtils.on;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LokaltKodeverkImplTest {

    LokaltKodeverkImpl kodeverk = new LokaltKodeverkImpl();

    @Test
    public void temaUtenTemagruppeSkalReturnereARBD() {
        assertThat(kodeverk.hentTemagruppeForTema("NOE SOM IKKE FINNES"), is("ARBD"));
    }

    @Test
    public void temaTilTemagruppeTest() {
        assertThat(kodeverk.hentTemagruppeForTema("AAP"), is("ARBD"));
        assertThat(kodeverk.hentTemagruppeForTema("FOS"), is("ARBD"));
        assertThat(kodeverk.hentTemagruppeForTema("TRK"), is("OVRG"));
        assertThat(kodeverk.hentTemagruppeForTema("UFO"), is("PENS"));
        assertThat(kodeverk.hentTemagruppeForTema("HEL"), is("HJLPM"));
    }

    @Test
    public void beggeMappingerHarLikeMangeInnslag() {
        int temaTilTemagruppe = kodeverk.hentTemaTemagruppeMapping().size();
        int temagruppeTilTema = on(kodeverk.hentTemagruppeTemaMapping()).flatmap(new Transformer<Entry<String, List<String>>, Iterable<String>>() {
            @Override
            public Iterable<String> transform(Entry<String, List<String>> stringListEntry) {
                return stringListEntry.getValue();
            }
        }).collect().size();

        assertThat(temagruppeTilTema, is(temaTilTemagruppe));
    }
}