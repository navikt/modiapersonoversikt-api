package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

public class SaksinformasjonPanel extends Panel {

    @Inject
    protected BulletProofKodeverkService kodeverk;

    @Inject
    private BulletproofCmsService cms;

    public SaksinformasjonPanel(String id, String temakode) {
        super(id);

        String temanavn = kodeverk.getTemanavnForTemakode(temakode, BulletProofKodeverkService.ARKIVTEMA);

        setVisible(cms.eksistererArtikkel("saksinformasjon." + temakode));

        add(
                createTemaLenker(temakode),
                createFasteLenker(),
                new Label("temaTittel", temanavn),
                new Label("saksinformasjonTittel", cms.hentTekst("saksinformasjon.tittel")),
                new Label("saksinformasjonTekst", cms.hentArtikkel("saksinformasjon." + temakode)).setEscapeModelStrings(false)
        );
    }

    private Component createTemaLenker(String temakode) {
        Label fasteLenker = new Label("temaLenker");
        String temaLenkerTekst = cms.hentArtikkel("saksinformasjon.lenker."+temakode);
        fasteLenker.setDefaultModel(new Model<>(temaLenkerTekst));
        fasteLenker.setEscapeModelStrings(false);
        fasteLenker.setVisible(cms.eksistererArtikkel("saksinformasjon.lenker."+temakode));
        return fasteLenker;
    }

    private Component createFasteLenker() {
        String fasteLenkerTekst = cms.hentArtikkel("saksinformasjon.lenker.ALLE");
        Label fasteLenker = new Label("fasteLenker", fasteLenkerTekst);
        fasteLenker.setEscapeModelStrings(false);
        return fasteLenker;
    }
}
