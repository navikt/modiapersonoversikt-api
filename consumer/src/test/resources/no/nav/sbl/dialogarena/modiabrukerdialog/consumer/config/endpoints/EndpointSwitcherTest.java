package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EndpointSwitcherTest {

    private Value value;

    interface Value {
        String gimme();
    }

    @Before
    public void setupProxyDillDall() {
        Value foo = new Value() {
            public String gimme() {
                return "foo";
            }
        };

        Value bar = new Value() {
            public String gimme() {
                return "bar";
            }
        };

        value = createSwitcher(foo, bar, "useBar", Value.class);
    }

    @After
    public void tearDown() throws Exception {
        System.getProperties().remove("useBar");
    }

    @Test
    public void useFirstObjectIfPrpertyKeyNotSpecified() throws Exception {
        assertThat(value.gimme(), is("foo"));
    }

    @Test
    public void useFirstObjectIfPropertyKeyIsSpecified() throws Exception {
        System.setProperty("useBar", "yes");
        assertThat(value.gimme(), is("bar"));
    }
}
