package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.liste.SoknadListe;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Oversikt extends Lerret {

    @Inject
    private SoknaderService soknaderService;

    public Oversikt(String id, String fnr) {
        super(id);
        boolean soknaderServiceCallFailed = false;
        List<Soknad> soknader = new ArrayList<>();

        try {
            soknader = soknaderService.getSoknader(fnr);
        } catch (ApplicationException ex) {
            soknaderServiceCallFailed = true;
        }


        add(new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))));
        add(new MeldingerWidget("meldinger", "M", fnr));
        add(new SykepengerWidget("sykepenger", "Y", new Model<>(fnr)));
        add(new SoknadListe("soknader", new ListModel<>(soknader), soknaderServiceCallFailed));

    }

}