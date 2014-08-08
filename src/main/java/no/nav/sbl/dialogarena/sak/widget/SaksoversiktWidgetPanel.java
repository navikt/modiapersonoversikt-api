package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static java.lang.String.format;
import static no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService.ARKIVTEMA;
import static no.nav.sbl.dialogarena.sak.util.SakDateFormatter.printLongDate;

public class SaksoversiktWidgetPanel extends GenericPanel<TemaVM> {

    @Inject
    private CmsContentRetriever cmsContentRetriever;

    @Inject
    private BulletProofKodeverkService kodeverk;

    public SaksoversiktWidgetPanel(String id, IModel<TemaVM> model) {
        super(id);
        TemaVM temaVM = model.getObject();

        DateTime sistOppdatert = temaVM.sistoppdaterteBehandling.behandlingDato;
        add(
                new Label("temaTittel", kodeverk.getTemanavnForTemakode(temaVM.temakode, ARKIVTEMA)),
                new Label("temaDato", format(cmsContentRetriever.hentTekst("behandling.opprettet.dato"), printLongDate(sistOppdatert)))
        );
    }
}
