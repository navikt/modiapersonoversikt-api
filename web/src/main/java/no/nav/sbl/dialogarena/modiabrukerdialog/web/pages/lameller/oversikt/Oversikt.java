package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sbl.dialogarena.soknader.liste.SoknadListe;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;

public class Oversikt extends Lerret {

    @Inject
    private SoknaderService soknaderService;

    public Oversikt(String id, String fnr) {
        super(id);
        add(
                new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))),
                new SykepengerWidget("sykepenger", "Y", new Model<>(fnr)),
                new SoknadListe("soknader", new ListModel<>(soknaderService.getSoknader("")))
        );
    }

}
