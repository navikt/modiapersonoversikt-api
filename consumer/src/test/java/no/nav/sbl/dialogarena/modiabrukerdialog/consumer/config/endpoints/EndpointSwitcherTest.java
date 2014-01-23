package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.System.getProperties;
import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EndpointSwitcherTest {

    public static final String USE_FOO = "useBar";
    private Value value;

    interface Value {
        String gimme();
    }

    @Before
    public void setupProxy() {
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

        setProperty("tillatmocksetup.url", "http://ja.no");
        value = createSwitcher(foo, bar, USE_FOO, Value.class);
    }

    @After
    public void tearDown() throws Exception {
        getProperties().remove(USE_FOO);
    }

    @Test
    public void useFirstObjectIfPropertyKeyNotSpecified() throws Exception {
        assertThat(value.gimme(), is("foo"));
    }

    @Test
    public void useFirstObjectIfPropertyKeyIsSpecifiedAndNo() throws Exception {
        setProperty(USE_FOO, "no");
        assertThat(value.gimme(), is("foo"));
    }

    @Test
    public void useSecondObjectIfPropertyKeyIsSpecifiedAndYes() throws Exception {
        setProperty(USE_FOO, "yes");
        assertThat(value.gimme(), is("bar"));
    }
}
