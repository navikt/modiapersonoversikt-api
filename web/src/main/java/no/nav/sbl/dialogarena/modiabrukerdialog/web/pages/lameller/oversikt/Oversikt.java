package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import static java.util.Arrays.asList;

public class Oversikt extends Lerret {

    public Oversikt(String id, String fnr) {
        super(id);
        add(
                new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))),
                new SykepengerWidget("sykepenger", "Y", new Model<>(fnr))
        );
    }

}
