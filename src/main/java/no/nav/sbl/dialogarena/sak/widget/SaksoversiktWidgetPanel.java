package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.service.interfaces.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.interfaces.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static java.lang.String.format;
import static no.nav.modig.modia.widget.utils.WidgetDateFormatter.date;
import static no.nav.sbl.dialogarena.sak.service.interfaces.BulletProofKodeverkService.ARKIVTEMA;

public class SaksoversiktWidgetPanel extends GenericPanel<TemaVM> {

    @Inject
    private BulletproofCmsService cms;

    @Inject
    private BulletProofKodeverkService kodeverk;

    public SaksoversiktWidgetPanel(String id, IModel<TemaVM> model) {
        super(id);
        TemaVM temaVM = model.getObject();

        DateTime sistOppdatert = temaVM.sistoppdaterteBehandling.behandlingDato;
        add(
                new Label("temaTittel", kodeverk.getTemanavnForTemakode(temaVM.temakode, ARKIVTEMA)),
                new Label("temaDato", format(cms.hentTekst("hendelse.sistoppdatert.dato"), date(sistOppdatert)))
        );
    }
}
