package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.HenvendelseService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class Innboks extends Lerret {

    public static final String VALGT_HENVENDELSE = "hendelser.valgt_henvendelse";
    public static final String OPPDATER_HENVENDELSER = "hendelser.oppdater_henvendelser";

    public static final JavaScriptResourceReference JS_REFERENCE = new JavaScriptResourceReference(Innboks.class, "innboks.js");

    @Inject
    HenvendelseService service;

    private InnboksModell innboksModell;
    private String fnr;

    public Innboks(String id, String fnr) {
        super(id);
        this.fnr = fnr;
        this.innboksModell = new InnboksModell(new InnboksVM(service.hentAlleHenvendelser(fnr)));
        setDefaultModel(innboksModell);
        setOutputMarkupId(true);

        final AlleHenvendelserPanel alleMeldinger = new AlleHenvendelserPanel("henvendelser", innboksModell);
        alleMeldinger.add(hasCssClassIf("skjult", innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm));
        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksModell);

        add(alleMeldinger, detaljvisning);
    }

    @RunOnEvents(OPPDATER_HENVENDELSER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterHenvendelserFra(service.hentAlleHenvendelser(fnr));
        target.add(this);
    }
}
