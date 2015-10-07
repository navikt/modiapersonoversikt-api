package no.nav.sbl.dialogarena.varsel.lamell;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class VarslerOversiktLink extends AjaxLink<String> {

    @Inject
    private VarslerService varselService;
    @Inject
    @Named("varsling-cms-integrasjon")
    private CmsContentRetriever cms;

    private static final int EN_UKE = 7;

    public VarslerOversiktLink(String id, String fnr) {
        super(id);
        Optional<List<Varsel>> varsler = varselService.hentAlleVarsler(fnr);

        int antallNyeVarsler = on(varsler.getOrElse(Collections.<Varsel>emptyList())).filter(erNyereEnn(EN_UKE)).collect().size();
        String cmsKeyForVarselLenke = hentCMSKeyForVarselLenke(antallNyeVarsler, varsler);
        String cmsTekstForVarselLenke = cms.hentTekst(cmsKeyForVarselLenke);

        add(new Label("varsling-tekst", format(cmsTekstForVarselLenke, antallNyeVarsler)));
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        send(VarslerOversiktLink.this, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_HEADER_CLICKED, new WidgetHeaderPayload("varsling")));
    }

    protected static String hentCMSKeyForVarselLenke(int antallNyeVarsler, Optional<List<Varsel>> varsler) {
        if (!varsler.isSome()) {
            return "varsler.oversikt.lenke.feil.uthenting";
        }

        if (varsler.get().isEmpty()) {
            return "varsler.oversikt.lenke.ingen.varsler";
        }
        return antallNyeVarsler > 0 ? "varsler.oversikt.lenke.nye.varsler" : "varsler.oversikt.lenke";
    }

    protected static Predicate<Varsel> erNyereEnn(final int antallDager) {
        return new Predicate<Varsel>() {
            @Override
            public boolean evaluate(Varsel varsel) {
                LocalDate now = LocalDate.now();
                int dagerSidenVarsel = Days.daysBetween(varsel.mottattTidspunkt.toLocalDate(), now).getDays();
                return dagerSidenVarsel <= antallDager;
            }
        };
    }
}
