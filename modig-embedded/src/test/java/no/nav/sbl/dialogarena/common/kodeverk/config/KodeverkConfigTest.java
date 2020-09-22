package no.nav.sbl.dialogarena.common.kodeverk.config;


import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

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
