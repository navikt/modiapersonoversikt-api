package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.HenvendelseService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class Innboks extends Lerret {

    public static final String VALGT_HENVENDELSE = "hendelser.valgt_henvendelse";
    public static final String OPPDATER_HENVENDELSER = "hendelser.oppdater_henvendelser";

    @Inject
    HenvendelseService service;

    private InnboksModell innboksModell;
    AjaxLink<Void> tilInnboksLink;

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Innboks.class, "innboks.js")));
    }

    public Innboks(String id) {
        super(id);
        innboksModell = new InnboksModell(new InnboksVM(service.hentAlleHenvendelser(SubjectHandler.getSubjectHandler().getUid())));
        setDefaultModel(innboksModell);
        setOutputMarkupId(true);

        final AlleHenvendelserPanel alleMeldinger = new AlleHenvendelserPanel("henvendelser", innboksModell, service);
        alleMeldinger.add(hasCssClassIf("skjult", innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm));
        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksModell);

        add(alleMeldinger, detaljvisning);
    }

    @RunOnEvents(OPPDATER_HENVENDELSER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterHenvendelserFra(service.hentAlleHenvendelser(SubjectHandler.getSubjectHandler().getUid()));
        target.add(this);
    }
}
