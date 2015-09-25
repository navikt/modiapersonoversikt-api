package no.nav.sbl.dialogarena.varsel.lamell;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class VarslerOversiktLink extends AjaxLink<String> {

    @Inject
    private VarslerService varselService;

    @Inject
    @Named("varsling-cms-integrasjon")
    private CmsContentRetriever cms;


    private static final int EI_UKE = 7;

    public VarslerOversiktLink(String id, String fnr) {
        super(id);

        add(new Label("varsling-tekst", nyeVarslerDenSisteUken(fnr)));
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        send(VarslerOversiktLink.this, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_HEADER_CLICKED, new WidgetHeaderPayload("varsling")));
    }

    private String nyeVarslerDenSisteUken(String fnr) {
        List<Varsel> varsler = varselService.hentAlleVarsler(fnr);

        if (varsler.isEmpty()) {
            return cms.hentTekst("varsler.oversikt.lenke.ingen.varsler");
        }

        int antallNyeVarsler = 0;
        LocalDate iDag = LocalDate.now();

        for (Varsel varsel : varsler) {
            int dagerSidenVarsel = Days.daysBetween(varsel.mottattTidspunkt.toLocalDate(), iDag).getDays();
            if (dagerSidenVarsel <= EI_UKE) {
                antallNyeVarsler++;
            }
        }

        String label = (antallNyeVarsler > 0) ?
                cms.hentTekst("varsler.oversikt.lenke.nye.varsler") :
                cms.hentTekst("varsler.oversikt.lenke");

        return String.format(label, antallNyeVarsler);
    }
}
