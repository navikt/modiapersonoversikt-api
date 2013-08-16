package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

import java.util.List;

import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.sendsporsmal.SendSporsmalPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.sendsporsmal.SideNavigerer;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.sendsporsmal.Sporsmal;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.sendsporsmal.SporsmalBekreftelsePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.sendsporsmal.TemavelgerPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Gir bruker mulighet til å sende inn spørsmål til NAV
 */
public class SporsmalOgSvarSide extends BasePage implements SideNavigerer {

    private static final String FODSELSNUMMER = "28088834986";

    private enum Side {INNBOKS_BRUKER, TEMAVELGER, SEND_SPORSMAL, SPORMSMAL_BEKREFTELSE}

    IModel<Side> aktivSide = new Model<>(Side.values()[0]);
    final List<String> alleTema = asList("Uføre", "Sykepenger", "Tjenestebasert innskuddspensjon", "Annet");
    CompoundPropertyModel<Sporsmal> model = new CompoundPropertyModel<>(new Sporsmal());

	public SporsmalOgSvarSide() {
        Innboks innboks = new Innboks("innboks-bruker", FODSELSNUMMER);
        innboks.add(visibleIf(aktivSideEr(Side.INNBOKS_BRUKER)));

        TemavelgerPanel temavelger = new TemavelgerPanel("temavelger", alleTema, model, this);
        temavelger.add(visibleIf(aktivSideEr(Side.TEMAVELGER)));

        SendSporsmalPanel sendSporsmal = new SendSporsmalPanel("send-sporsmal", model, FODSELSNUMMER, this);
        sendSporsmal.add(visibleIf(aktivSideEr(Side.SEND_SPORSMAL)));

        SporsmalBekreftelsePanel sporsmalBekreftelse = new SporsmalBekreftelsePanel("sporsmal-bekreftelse", model, this);
        sporsmalBekreftelse.add(visibleIf(aktivSideEr(Side.SPORMSMAL_BEKREFTELSE)));

        AjaxLink<Object> innboksLink = new AjaxLink<Object>("innboks-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                aktivSide.setObject(Side.INNBOKS_BRUKER);
                target.add(SporsmalOgSvarSide.this);
            }
        };
        innboksLink.add(visibleIf(not(aktivSideEr(Side.INNBOKS_BRUKER))));

        AjaxLink<Object> skrivNyLink = new AjaxLink<Object>("skriv-ny-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                aktivSide.setObject(Side.TEMAVELGER);
                model.setObject(new Sporsmal());
                target.add(SporsmalOgSvarSide.this);
            }
        };
        skrivNyLink.add(visibleIf(aktivSideEr(Side.INNBOKS_BRUKER)));

        WebMarkupContainer topBar = new WebMarkupContainer("top-bar");
        topBar.add(innboksLink, skrivNyLink);

        add(topBar, innboks, temavelger, sendSporsmal, sporsmalBekreftelse);
    }

    private IModel<Boolean> aktivSideEr(final Side side) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return aktivSide.getObject() == side;
            }
        };
    }

    @Override
    public void neste() {
    	aktivSide.setObject(Side.values()[aktivSide.getObject().ordinal() + 1]);
    }

    @Override
    public void forside() {
        aktivSide.setObject(Side.values()[0]);
    }

}
