package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator.MERGEABLE_TITTEL;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator.MERGEABLE_TITTEL_ANTALL_SATS;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling1;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling2;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling3;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling4;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling5;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling6;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling7;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling8;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.getWsUtbetalinger;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformer.createTransformObjekter;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformer.createUtbetalinger;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformer.transformerSkatt;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.MergeUtil.merge;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UtbetalingTransformerTest {

    private static final String FORSKUDDSTREKK_SKATT = "Forskuddstrekk skatt";
    private static final String GRUNNBELOP = "Grunnbeløp";
    private static final String FNR = "12345678978";

    @BeforeClass
    public static void setUp() throws Exception {
        getWsUtbetalinger(FNR, new DateTime().minusMonths(6), new DateTime());
    }

    @Test
    public void testUtbetalingerBlirTransformertTilTransformObjekter() throws Exception {
        WSUtbetaling wsUtbetaling = createUtbetaling1();

        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(asList(wsUtbetaling), FNR);

        assertThat(transformObjekter.size(), is(4));
        assertThat(transformObjekter.get(0).getUtbetalingsdato(), is(wsUtbetaling.getUtbetalingDato()));
        assertThat(transformObjekter.get(0).getKontonummer(), is(wsUtbetaling.getGironr()));
        assertThat(transformObjekter.get(0).getValuta(), is(wsUtbetaling.getValuta()));
        assertThat(transformObjekter.get(0).getMottaker(), is(wsUtbetaling.getUtbetalingMottaker().getNavn()));
        assertThat(transformObjekter.get(0).getMottakerId(), is(wsUtbetaling.getUtbetalingMottaker().getMottakerId()));
        assertThat(transformObjekter.get(0).getUtbetalingsdato(), is(wsUtbetaling.getUtbetalingDato()));

        Double detaljBelop1 = wsUtbetaling.getBilagListe().get(0).getPosteringsdetaljerListe().get(0).getBelop();
        Double detaljBelop2 = wsUtbetaling.getBilagListe().get(0).getPosteringsdetaljerListe().get(1).getBelop();
        Double detaljBelop3 = wsUtbetaling.getBilagListe().get(1).getPosteringsdetaljerListe().get(0).getBelop();
        Double detaljBelop4 = wsUtbetaling.getBilagListe().get(1).getPosteringsdetaljerListe().get(1).getBelop();
        assertThat(transformObjekter.get(0).getBelop(), is(detaljBelop1));
        assertThat(transformObjekter.get(1).getBelop(), is(detaljBelop2));
        assertThat(transformObjekter.get(2).getBelop(), is(detaljBelop3));
        assertThat(transformObjekter.get(3).getBelop(), is(detaljBelop4));
    }

    @Test
    public void testSkattBlirLagtSomUnderYtelse() throws Exception {
        WSUtbetaling wsUtbetaling = createUtbetaling1();

        transformerSkatt(asList(wsUtbetaling));
        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(asList(wsUtbetaling), FNR);

        final String dagpenger = "Dagpenger";
        List<String> underytelser = asList(GRUNNBELOP, FORSKUDDSTREKK_SKATT, GRUNNBELOP, FORSKUDDSTREKK_SKATT);
        int index = 0;
        for (UtbetalingTransformObjekt transformObjekt : transformObjekter) {
            assertThat(transformObjekt.getHovedYtelse().equalsIgnoreCase(dagpenger), is(true));
            assertThat(transformObjekt.getUnderYtelse().equalsIgnoreCase(underytelser.get(index)), is(true));
            index++;
        }
    }

    @Test
    public void mottakerBlirOversatt_NullMottaker_GirBruker() throws Exception {
        String mottaker = UtbetalingTransformer.transformMottakerKode(null, FNR);
        assertThat(mottaker, is(BRUKER));
    }

    @Test
    public void mottakerBlirOversatt_Mottaker_GirBruker() throws Exception {
        WSMottaker wsMottaker = createUtbetaling1().getUtbetalingMottaker();
        String mottaker = UtbetalingTransformer.transformMottakerKode(wsMottaker, wsMottaker.getMottakerId());
        assertThat(mottaker, is(BRUKER));
    }

    @Test
    public void mottakerBlirOversatt_IkkeMottaker_GirArbeidsgiver() throws Exception {
        WSMottaker wsMottaker = createUtbetaling1().getUtbetalingMottaker();
        String mottaker = UtbetalingTransformer.transformMottakerKode(wsMottaker, "21345");
        assertThat(mottaker, is(ARBEIDSGIVER));
    }

    @Test
    public void lagUtbetalinger_FireYtelser_Gir_EnUtbetaling() throws Exception {
        List<Utbetaling> utbetalinger = createUtbetalinger(asList(createUtbetaling1()), FNR);

        assertThat(utbetalinger.size(), is(1));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(2));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getTittel(), is(GRUNNBELOP));
        assertThat(utbetalinger.get(0).getUnderytelser().get(1).getTittel(), is(FORSKUDDSTREKK_SKATT));
    }

    @Test
    public void lagUtbetalinger_Ytelser_Gir_FlereUtbetalinger() throws Exception {
        List<Utbetaling> utbetalinger = createUtbetalinger(asList(createUtbetaling1(), createUtbetaling3(), createUtbetaling2()), FNR);

        assertThat(utbetalinger.size(), is(4));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Uføre"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(3));
        assertThat(utbetalinger.get(0).getBrutto(), is(2500.0));
        assertThat(utbetalinger.get(0).getTrekk(), is(-1700.0));
        assertThat(utbetalinger.get(0).getUtbetalt(), is(800.0));

        assertThat(utbetalinger.get(1).getHovedytelse(), is("Foreldrepenger"));
        assertThat(utbetalinger.get(1).getUnderytelser().size(), is(1));
        assertThat(utbetalinger.get(1).getBrutto(), is(1000.0));
        assertThat(utbetalinger.get(1).getTrekk(), is(0.0));
        assertThat(utbetalinger.get(1).getUtbetalt(), is(1000.0));

        assertThat(utbetalinger.get(2).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(2).getUnderytelser().size(), is(2));
        assertThat(utbetalinger.get(2).getBrutto(), is(2000.0));
        assertThat(utbetalinger.get(2).getTrekk(), is(-700.0));
        assertThat(utbetalinger.get(2).getUtbetalt(), is(1300.0));

        assertThat(utbetalinger.get(3).getHovedytelse(), is("Foreldrepenger"));
        assertThat(utbetalinger.get(3).getUnderytelser().size(), is(2));
        assertThat(utbetalinger.get(3).getBrutto(), is(1000.0));
        assertThat(utbetalinger.get(3).getTrekk(), is(-350.0));
        assertThat(utbetalinger.get(3).getUtbetalt(), is(650.0));
    }

    @Test
    public void lagUtbetalinger_slaaSammenUnderYtelserMedSammeTittel() throws Exception {
        List<Utbetaling> utbetalinger = createUtbetalinger(asList(createUtbetaling1()), FNR);
        String info = "Ekstra detaljinfo";

        assertThat(utbetalinger.size(), is(1));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(2));


        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getTittel(), is(GRUNNBELOP));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getSpesifikasjon(), is(info));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getAntall(), is(12));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getSats(), is(123.0));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getBelop(), is(2000.0));

        assertThat(utbetalinger.get(0).getUnderytelser().get(1).getTittel(), is(FORSKUDDSTREKK_SKATT));
        assertThat(utbetalinger.get(0).getUnderytelser().get(1).getSpesifikasjon(), is(""));
        assertThat(utbetalinger.get(0).getUnderytelser().get(1).getAntall(), is(1));
        assertThat(utbetalinger.get(0).getUnderytelser().get(1).getSats(), is(1.0));
        assertThat(utbetalinger.get(0).getUnderytelser().get(1).getBelop(), is(-700.0));
    }

    @Test
    public void lagMangeUtbetalinger() throws Exception {
        List<Utbetaling> utbetalinger = createUtbetalinger(asList(createUtbetaling1(),
                createUtbetaling2(),
                createUtbetaling3(),
                createUtbetaling4(),
                createUtbetaling5(),
                createUtbetaling6(),
                createUtbetaling7(),
                createUtbetaling8()),
                FNR);

        final String ytelse = "Høreapparater";
        final String ytelse1 = "Dagpenger";
        final String ytelse2 = "Uføre";
        final String ytelse3 = "Foreldrepenger";
        List<Utbetaling> horeApparater = filtrer(utbetalinger, ytelse);
        List<Utbetaling> dagpenger = filtrer(utbetalinger, ytelse1);
        List<Utbetaling> ufore = filtrer(utbetalinger, ytelse2);
        List<Utbetaling> foreldrepenger = filtrer(utbetalinger, ytelse3);

        assertThat(utbetalinger.size(), is(8));
        assertThat(horeApparater.size(), is(1));
        assertThat(horeApparater.get(0).getHovedytelse(), is(ytelse));
        assertThat(dagpenger.size(), is(4));
        assertThat(ufore.size(), is(1));
        assertThat(foreldrepenger.size(), is(2));
    }

    @Test
    public void testLeggSammenUnderYtelser_MangeUnderYtelser_BlirSlaattSammen() throws Exception {
        String spesifikasjon = "Ekstra opplysning";
        String spesifikasjon1 = "Mer info";
        Underytelse ytelse1 = new Underytelse("Rød", spesifikasjon, 1, 1000.0, 1.0);
        Underytelse ytelse2 = new Underytelse("Grønn", spesifikasjon, 1, 1000.0, 1.0);
        Underytelse ytelse3 = new Underytelse("Blå", spesifikasjon, 1, 100.0, 1.0);
        Underytelse ytelse4 = new Underytelse("Rød", spesifikasjon1, 2, 10.0, 1.0);
        Underytelse ytelse5 = new Underytelse("Rød", spesifikasjon1, 1, 10.0, 1.0);

        List<Underytelse> underytelser = merge(new ArrayList<Mergeable<Underytelse>>(asList(ytelse1, ytelse2, ytelse3, ytelse4, ytelse5)),
                MERGEABLE_TITTEL,
                MERGEABLE_TITTEL);

        assertThat(underytelser.size(), is(3));
        assertThat(underytelser.get(0).getTittel(), is("Blå"));
        assertThat(underytelser.get(1).getTittel(), is("Grønn"));
        assertThat(underytelser.get(2).getTittel(), is("Rød"));
        assertThat(underytelser.get(2).getBelop(), is(1020.0));
        assertThat(underytelser.get(2).getSpesifikasjon(), is(spesifikasjon + ". " + spesifikasjon1));
    }

    @Test
    public void testLeggSammenUnderYtelser_BareEnUnderytelse_BlirMedIResultat() throws Exception {
        String spesifikasjon = "Ekstra opplysning";
        Underytelse ytelse1 = new Underytelse("Rød", spesifikasjon, 1, 1000.0, 1.0);

        List<Underytelse> underytelser = merge(new ArrayList<Mergeable<Underytelse>>(asList(ytelse1)),
                MERGEABLE_TITTEL_ANTALL_SATS,
                MERGEABLE_TITTEL);

        assertThat(underytelser.size(), is(1));
        assertThat(underytelser.get(0).getTittel(), is("Rød"));
        assertThat(underytelser.get(0).getBelop(), is(1000.0));
        assertThat(underytelser.get(0).getSpesifikasjon(), is(spesifikasjon));
    }

    @Test
    public void testLeggSammenUnderYtelser_ToUnderYtelserMedForskjelligTittel_BlirIkkeSlaattSammen() throws Exception {
        String spesifikasjon = "Ekstra opplysning";
        Underytelse ytelse1 = new Underytelse("Rød", spesifikasjon, 1, 1000.0, 1.0);
        Underytelse ytelse2 = new Underytelse("Grønn", spesifikasjon, 1, 1000.0, 1.0);

        List<Underytelse> underytelser = merge(new ArrayList<Mergeable<Underytelse>>(asList(ytelse1, ytelse2)),
                MERGEABLE_TITTEL_ANTALL_SATS,
                MERGEABLE_TITTEL);

        assertThat(underytelser.size(), is(2));
        assertThat(underytelser.get(0).getTittel(), is("Grønn"));
        assertThat(underytelser.get(0).getBelop(), is(1000.0));
        assertThat(underytelser.get(1).getTittel(), is("Rød"));
        assertThat(underytelser.get(1).getBelop(), is(1000.0));
        assertThat(underytelser.get(1).getSpesifikasjon(), is(spesifikasjon));
    }

    private List<Utbetaling> filtrer(List<Utbetaling> utbetalinger, String ytelse) {
        Predicate<Utbetaling> harYtelse = lagStringPredikat(ytelse);
        return on(utbetalinger).filter(harYtelse).collect();
    }

    private Predicate<Utbetaling> lagStringPredikat(final String ytelse) {
        return new Predicate<Utbetaling>() {
            public boolean evaluate(Utbetaling object) {
                return ytelse.equalsIgnoreCase(object.getHovedytelse());
            }
        };
    }
}
