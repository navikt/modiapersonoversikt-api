package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;

public class SaksinformasjonPanel extends Panel {

    @Inject
    protected BulletProofKodeverkService kodeverk;

    @Inject
    private BulletproofCmsService cms;

    public SaksinformasjonPanel(String id, String temakode) {
        super(id);

        setVisible(cms.eksistererArtikkel("saksinformasjon." + temakode));

        add(
                new Label("saksinformasjonTekst", cms.hentArtikkel("saksinformasjon." + temakode)).setEscapeModelStrings(false)
        );
    }
}
