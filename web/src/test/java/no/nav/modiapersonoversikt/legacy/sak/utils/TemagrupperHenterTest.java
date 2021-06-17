package no.nav.modiapersonoversikt.legacy.sak.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class TemagrupperHenterTest {

    private TemagrupperHenter temagrupperHenter = new TemagrupperHenter();

    @Before
    public void setup(){
        System.setProperty("saksoversikt.temagrupper","ARBEID,FAMILIE");
        System.setProperty("saksoversikt.temagrupper.ARBEID.navn","Arbeid");
        System.setProperty("saksoversikt.temagrupper.ARBEID.temaer","DAG,OPP,SYK,SYM,TIL,AAP,IND,MOB,UFO,YRK");
        System.setProperty("saksoversikt.temagrupper.FAMILIE.navn","Famile");
        System.setProperty("saksoversikt.temagrupper.FAMILIE.temaer","OMS");
    }

    @Test
    public void temagrupperErKorrekte(){
        Map<String, List<String>> temagrupper = temagrupperHenter.genererTemagrupperMedTema();

        assertThat(temagrupper.get("ARBEID").size(), greaterThan(2));
        assertTrue(temagrupper.get("ARBEID").contains("DAG"));
    }

}
