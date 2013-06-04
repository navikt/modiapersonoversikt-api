package no.nav.dialogarena.modiabrukerdialog.example.component;

import javax.inject.Inject;

public class ExampleComponent {

    @Inject
    private String name;

    public ExampleComponent() {
        name.toString(); //Allows debug-inspection
    }


}
