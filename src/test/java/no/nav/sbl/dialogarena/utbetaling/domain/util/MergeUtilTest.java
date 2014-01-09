package no.nav.sbl.dialogarena.utbetaling.domain.util;


import no.nav.sbl.dialogarena.utbetaling.domain.transform.Mergeable;
import org.apache.commons.collections15.Transformer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumInt;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.MergeUtil.merge;
import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MergeUtilTest {

    @Test
    public void kanMerge_EnMergeable() throws Exception {
        Farge rod = new Farge(1, "Rød");

        List<Farge> blandede_farger = merge(new ArrayList<Mergeable<Farge>>(asList(rod)), Farge.MERGEABLE_NAVN, Farge.MERGEABLE_NAVN);

        assertThat(blandede_farger.size(), is(1));
        assertThat(blandede_farger.get(0).navn, is("Rød"));
        assertThat(blandede_farger.get(0).hue, is(1));
    }

    @Test
    public void kanMerge_MangeLikeMergeables() throws Exception {
        Farge gronn1 = new Farge(109, "Grønn");
        Farge gronn2 = new Farge(100, "Grønn");
        Farge gronn3 = new Farge(115, "Grønn");

        List<Farge> blandede_farger = merge(new ArrayList<Mergeable<Farge>>(asList(gronn2, gronn1, gronn2, gronn3)), Farge.MERGEABLE_NAVN, Farge.MERGEABLE_NAVN);

        assertThat(blandede_farger.size(), is(1));
        assertThat(blandede_farger.get(0).navn, is("Grønn"));
        assertThat(blandede_farger.get(0).hue, is(106));
    }

    @Test
    public void kanMerge_MangeForskjelligeMergeables() throws Exception {
        Farge rod = new Farge(1, "Rød");
        Farge gul = new Farge(60, "Gul");
        Farge gronn1 = new Farge(109, "Grønn");
        Farge gronn2 = new Farge(100, "Grønn");
        Farge gronn3 = new Farge(115, "Grønn");

        List<Farge> blandede_farger = merge(new ArrayList<Mergeable<Farge>>(asList(rod, gronn1, gul, gronn1, gronn2, gronn3)), Farge.MERGEABLE_NAVN, Farge.MERGEABLE_NAVN);

        List<String> navn = asList("Grønn", "Gul", "Rød");
        List<Integer> hues = asList(   108,    60,     1);
        assertThat(blandede_farger.size(), is(3));
        assertThat(blandede_farger.get(0).navn, is(navn.get(0)));
        assertThat(blandede_farger.get(0).hue, is(hues.get(0)));
        assertThat(blandede_farger.get(1).navn, is(navn.get(1)));
        assertThat(blandede_farger.get(1).hue, is(hues.get(1)));
        assertThat(blandede_farger.get(2).navn, is(navn.get(2)));
        assertThat(blandede_farger.get(2).hue, is(hues.get(2)));
    }

    private static final class Farge implements Mergeable<Farge> {
        String navn;
        int hue;

        private Farge(int hue, String navn) {
            this.hue = hue;
            this.navn = navn;
        }

        @Override
        public Farge doMerge(List<Mergeable> skalMerges) {
            List<Farge> farger = on(skalMerges).map(FARGE_TRANSFORMER).collectIn(new ArrayList<Farge>());
            if(farger.isEmpty()) { return null; }

            double sum = on(farger).map(HUE).reduce(sumInt);
            Double average = sum/farger.size();
            Set<String> navn = on(farger).map(NAVN).collectIn(new HashSet<String>());
            String fargeNavn = join(navn, ", ");
            return new Farge(Double.valueOf(Math.floor(average)).intValue(), fargeNavn);
        }

        static final Comparator<Mergeable<Farge>> MERGEABLE_NAVN = new Comparator<Mergeable<Farge>>() {
            @Override
            public int compare(Mergeable<Farge> o1, Mergeable<Farge> o2) {
                Farge obj1 = (Farge) o1;
                Farge obj2 = (Farge) o2;
                return obj1.navn.compareToIgnoreCase(obj2.navn);
            }
        };

        static final Transformer<Mergeable, Farge> FARGE_TRANSFORMER = new Transformer<Mergeable, Farge>() {
            @Override
            public Farge transform(Mergeable mergeable) {
                return (Farge) mergeable;
            }
        };

        static final Transformer<Farge, Integer> HUE = new Transformer<Farge, Integer>() {
            @Override
            public Integer transform(Farge farge) {
                return farge.hue;
            }
        };

        static final Transformer<Farge, String> NAVN = new Transformer<Farge, String>() {
            @Override
            public String transform(Farge farge) {
                return farge.navn;
            }
        };


    }

}
