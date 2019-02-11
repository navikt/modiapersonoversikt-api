import no.nav.apiapp.ApiApp;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;

public class Main {

    public static void main(String... args) {
        ApiApp.startApp(ModiaApplicationContext.class, args);
    }

}
