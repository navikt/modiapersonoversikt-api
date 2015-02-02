package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import org.apache.commons.collections15.Transformer;
import org.junit.Test;

import java.util.List;
import java.util.Map.Entry;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.ORT_HJE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.OVRG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LokaltKodeverkImplTest {

    LokaltKodeverkImpl kodeverk = new LokaltKodeverkImpl();

    @Test
    public void temaUtenTemagruppeSkalReturnereDefault() {
        assertThat(kodeverk.hentTemagruppeForTema("NOE SOM IKKE FINNES"), is(LokaltKodeverkImpl.TEMA_UTEN_TEMAGRUPPE.name()));
    }

    @Test
    public void temaTilTemagruppeTest() {
        assertThat(kodeverk.hentTemagruppeForTema("AAP"), is(ARBD.name()));
        assertThat(kodeverk.hentTemagruppeForTema("FOS"), is(ARBD.name()));
        assertThat(kodeverk.hentTemagruppeForTema("HEL"), is(ORT_HJE.name()));
        assertThat(kodeverk.hentTemagruppeForTema("TRK"), is(OVRG.name()));
        assertThat(kodeverk.hentTemagruppeForTema("UFO"), is("PENS"));
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