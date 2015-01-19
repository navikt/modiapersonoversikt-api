package no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain;


import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SakerTest {

    @Test
    public void skalReturnereFalseOmDetIkkeFinnesSaker() {
        SakerListe fagsaker = new SakerListe(new ArrayList<SakerForTema>());
        SakerListe generelleSaker = new SakerListe(new ArrayList<SakerForTema>());

        Saker saker = new Saker(fagsaker, generelleSaker);

        assertThat(saker.sakerFinnes(), is(false));
    }

    @Test
    public void skalReturnereFalseOmSakerListerErNull() {
        Saker saker = new Saker(null, null);

        assertThat(saker.sakerFinnes(), is(false));
    }

    @Test
    public void skalReturnereTrueOmDetFinnesSaker() {
        SakerListe fagsaker = new SakerListe(asList(
                new SakerForTema()
        ));
        SakerListe generelleSaker = new SakerListe(asList(
                new SakerForTema()
        ));

        Saker finnesFagsaker = new Saker(fagsaker, null);
        Saker finnesGenerellesaker = new Saker(null, generelleSaker);

        assertThat(finnesFagsaker.sakerFinnes(), is(true));
        assertThat(finnesGenerellesaker.sakerFinnes(), is(true));
    }
}