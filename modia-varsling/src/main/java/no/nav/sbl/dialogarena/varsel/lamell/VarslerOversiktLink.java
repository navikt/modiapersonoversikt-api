package no.nav.sbl.dialogarena.varsel.lamell;

import no.nav.modig.content.ContentRetriever;
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
import java.util.Optional;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class VarslerOversiktLink extends AjaxLink<String> {

    @Inject
    private VarslerService varselService;
    @Inject
    @Named("varsling-cms-integrasjon")
    private ContentRetriever cms;

    private static final int EN_UKE = 7;

    public VarslerOversiktLink(String id, String fnr) {
        super(id);
        Optional<List<Varsel>> varsler = varselService.hentAlleVarsler(fnr);


        int antallNyeVarsler = 0;
        if (varsler.isPresent()) {
            antallNyeVarsler = varsler.get().stream().filter(erNyereEnn(EN_UKE)).collect(toList()).size();
        }

        String cmsKeyForVarselLenke = hentCMSKeyForVarselLenke(antallNyeVarsler, varsler);
        String cmsTekstForVarselLenke = cms.hentTekst(cmsKeyForVarselLenke);

        add(new Label("varsling-tekst", format(cmsTekstForVarselLenke, antallNyeVarsler)));
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        send(VarslerOversiktLink.this, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_HEADER_CLICKED, new WidgetHeaderPayload("varsling")));
    }

    protected static String hentCMSKeyForVarselLenke(int antallNyeVarsler, Optional<List<Varsel>> varsler) {
        if (!varsler.isPresent()) {
            return "varsler.oversikt.lenke.feil.uthenting";
        }

        if (varsler.get().isEmpty()) {
            return "varsler.oversikt.lenke.ingen.varsler";
        }
        return antallNyeVarsler > 0 ? "varsler.oversikt.lenke.nye.varsler" : "varsler.oversikt.lenke";
    }

    protected static Predicate<Varsel> erNyereEnn(final int antallDager) {
        return varsel -> {
            LocalDate now = LocalDate.now();
            int dagerSidenVarsel = Days.daysBetween(varsel.mottattTidspunkt.toLocalDate(), now).getDays();
            return dagerSidenVarsel <= antallDager;
        };
    }
}
