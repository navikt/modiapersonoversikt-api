package no.nav.modiapersonoversikt.consumer.kodeverk2.config;


import no.nav.modiapersonoversikt.consumer.kodeverk2.Kodeverk;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KodeverkConfig.class})
public class KodeverkConfigTest {

    @Autowired
    private Kodeverk kodeverk;

    @Test
    public void kodeverkGetsInjected(){
        assertThat(kodeverk, is(notNullValue()));
    }

}
