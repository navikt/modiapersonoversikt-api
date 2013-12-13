package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringProperties;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.IngenUtbetalingerPanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsHolder;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelser;
import static no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere.ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere.HOVEDYTELSER_ENDRET;

public class UtbetalingLerret extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");
    @Inject
    private UtbetalingsHolder utbetalingsHolder;

    @Inject
    private String arenaUtbetalingUrl;

    private FilterParametere filterParametere;
    private FilterFormPanel filterFormPanel;
    private OppsummeringPanel totalOppsummeringPanel;
    private MarkupContainer utbetalingslisteContainer;
    private IngenUtbetalingerPanel ingenutbetalinger;


    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(
                new ExternalLink("arenalink", arenaUtbetalingUrl + fnr),
                filterFormPanel,
                totalOppsummeringPanel.setOutputMarkupPlaceholderTag(true),
                ingenutbetalinger.setOutputMarkupPlaceholderTag(true),
                utbetalingslisteContainer.setOutputMarkupId(true)
        );
    }

    private void instansierFelter(String fnr) {
        List<Utbetaling> utbetalinger = utbetalingsHolder.withFnr(fnr).hentUtbetalinger(defaultStartDato(), defaultSluttDato());
        filterParametere = new FilterParametere(defaultStartDato(), defaultSluttDato(), true, true, hentYtelser(utbetalinger));

        List<Utbetaling> synligeUtbetalinger = utbetalingsHolder.getResultat().getSynligeUtbetalinger(filterParametere);
        totalOppsummeringPanel = createTotalOppsummeringPanel(synligeUtbetalinger);
        ingenutbetalinger = new IngenUtbetalingerPanel("ingenutbetalinger");
        utbetalingslisteContainer = new WebMarkupContainer("utbetalingslisteContainer").add(opprettMaanedsPanelListe());
        endreSynligeKomponenter(synligeUtbetalinger.isEmpty());
        
        filterFormPanel = new FilterFormPanel("filterFormPanel", filterParametere);
        filterFormPanel.setOutputMarkupId(true);
    }

    private OppsummeringPanel createTotalOppsummeringPanel(List<Utbetaling> liste) {
        return new OppsummeringPanel("totalOppsummeringPanel",
                new CompoundPropertyModel<>(new OppsummeringProperties(liste, filterParametere.getStartDato(), filterParametere.getSluttDato())), true);
    }

    private Component opprettMaanedsPanelListe() {
        List<List<Utbetaling>> maanedsListe = utbetalingsHolder.getResultat().hentFiltrertUtbetalingerPerMaaned(filterParametere);
        return new ListView<List<Utbetaling>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Utbetaling>> item) {
                item.add(new MaanedsPanel("maanedsPanel", item.getModelObject(), filterParametere));
            }
        }.setOutputMarkupId(true);
    }

    @RunOnEvents(ENDRET)
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        List<Utbetaling> alleUtbetalinger = utbetalingsHolder.getResultat().hentUtbetalinger(filterParametere.getStartDato(), filterParametere.getSluttDato());
        filterParametere.setYtelser(hentYtelser(alleUtbetalinger));
        sendYtelserEndretEvent();

        List<Utbetaling> synligeUtbetalinger = utbetalingsHolder.getResultat().getSynligeUtbetalinger(filterParametere);
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringProperties(
                synligeUtbetalinger,
                filterParametere.getStartDato(),
                filterParametere.getSluttDato()));
        endreSynligeKomponenter(synligeUtbetalinger.isEmpty());
        utbetalingslisteContainer.addOrReplace(opprettMaanedsPanelListe());
        target.add(totalOppsummeringPanel, ingenutbetalinger, utbetalingslisteContainer);
    }

    @RunOnEvents(FilterFormPanel.FEIL)
    private void skjulSnurrepippVedFeil(AjaxRequestTarget target) {
        target.add(totalOppsummeringPanel);
    }

    private void endreSynligeKomponenter(boolean ingenSynligeUtbetalinger) {
        totalOppsummeringPanel.setVisibilityAllowed(!ingenSynligeUtbetalinger);
        ingenutbetalinger.setVisibilityAllowed(ingenSynligeUtbetalinger);
    }

    private void sendYtelserEndretEvent() {
        send(getPage(), Broadcast.DEPTH, HOVEDYTELSER_ENDRET);
    }


}
