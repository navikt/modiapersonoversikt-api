package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;

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

        EndpointSwitcher switcher = new EndpointSwitcher(foo, bar, "useBar");
        value = getValue(switcher);
    }

    @After
    public void tearDown() throws Exception {
        System.setProperty("useBar", "");
    }

    private Value getValue(EndpointSwitcher switcher) {
        return (Value) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Value.class}, switcher);
    }

    @Test
    public void useFirstObjectIfPrpertyKeyNotSpecified() throws Exception {
        assertThat(value.gimme(), is("foo"));
    }

    @Test
    public void useFirstObjectIfPropertyKeyIsSpecified() throws Exception {
        System.setProperty("useBar", "no");
        assertThat(value.gimme(), is("bar"));
    }
}
