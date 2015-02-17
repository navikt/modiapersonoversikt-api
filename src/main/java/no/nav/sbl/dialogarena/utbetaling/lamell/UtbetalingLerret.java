package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.TotalOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.UtbetalingerMessagePanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned.MaanedsPanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.HovedytelseUtils.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FILTER_ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.HOVEDYTELSER_ENDRET;

public final class UtbetalingLerret extends Lerret {

    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingLerret.class);

    public static final PackageResourceReference UTBETALING_LESS = new PackageResourceReference(UtbetalingLerret.class, "utbetaling.less");
    public static final JavaScriptResourceReference UTBETALING_LAMELL_JS = new JavaScriptResourceReference(UtbetalingLerret.class, "utbetaling.js");
    private static final String FEILMELDING_DEFAULT_KEY = "feil.utbetalinger.default";

    @Inject
    private String arenaUtbetalingUrl;
    @Inject
    private UtbetalingService service;

    private FilterParametere filterParametere;
    private TotalOppsummeringPanel totalOppsummeringPanel;
    private WebMarkupContainer utbetalingslisteContainer;
    private UtbetalingerMessagePanel ingenutbetalinger;
    private UtbetalingerMessagePanel feilmelding;
    private String fnr;

    public UtbetalingLerret(String id, String fnr) {
        super(id);
        instansierFelter(fnr);

        add(createArenaLenke(fnr),
                createFilterFormPanel(),
                totalOppsummeringPanel,
                utbetalingslisteContainer,
                ingenutbetalinger,
                feilmelding);
    }

    private ExternalLink createArenaLenke(String fnr) {
        return new ExternalLink("arenalink", arenaUtbetalingUrl + fnr) {
            @Override
            public void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("target", "_blank");
            }
        };
    }

    private void instansierFelter(String fnr) {
        this.fnr = fnr;
        ingenutbetalinger = createIngenUtbetalingerPanel();
        ingenutbetalinger.setOutputMarkupPlaceholderTag(true);

        feilmelding = createFeilmeldingPanel();
        feilmelding.setOutputMarkupPlaceholderTag(true).setVisibilityAllowed(false);

        List<Record<Hovedytelse>> ytelser = getHovedytelseListe(fnr, defaultStartDato(), defaultSluttDato());
        filterParametere = new FilterParametere(hovedytelseToYtelsebeskrivelse(ytelser));

        List<Record<Hovedytelse>> synligeUtbetalinger = on(ytelser).filter(filterParametere).collect();
        totalOppsummeringPanel = createTotalOppsummeringPanel(synligeUtbetalinger);

        utbetalingslisteContainer = createUtbetalinglisteContainer();
        utbetalingslisteContainer.add(createMaanedsPanelListe());
        utbetalingslisteContainer.setOutputMarkupPlaceholderTag(true);

        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());
    }

    private List<Record<Hovedytelse>> getHovedytelseListe(String fnr, LocalDate startDato, LocalDate sluttDato) {
        try {
            return service.hentUtbetalinger(fnr, startDato, sluttDato);
        } catch (ApplicationException | SystemException e) {
            LOG.warn("Noe feilet ved henting av utbetalinger for fnr {}", fnr, e);

            if (e.getId() != null) {
                feilmelding.endreMessageKey(e.getId());
            } else {
                feilmelding.endreMessageKey(FEILMELDING_DEFAULT_KEY);
            }

            feilmelding.setVisibilityAllowed(true);
            return new ArrayList<>();
        }
    }

    private FilterFormPanel createFilterFormPanel() {
        return (FilterFormPanel) new FilterFormPanel("filterFormPanel", filterParametere).setOutputMarkupId(true);
    }

    private TotalOppsummeringPanel createTotalOppsummeringPanel(List<Record<Hovedytelse>> liste) {
        return new TotalOppsummeringPanel("totalOppsummeringPanel", new OppsummeringVM(liste, filterParametere.getStartDato(), filterParametere.getSluttDato()));
    }

    private ListView<List<Record<Hovedytelse>>> createMaanedsPanelListe() {
        List<List<Record<Hovedytelse>>> maanedsListe = splittUtbetalingerPerMaaned(on(hentHovedytelseListe()).filter(filterParametere).collectIn(new ArrayList<Record<Hovedytelse>>()));
        return new ListView<List<Record<Hovedytelse>>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Record<Hovedytelse>>> item) {
                item.add(new MaanedsPanel("maanedsPanel", item.getModelObject()));
                item.add(visibleIf(new Model<>(!item.getModelObject().isEmpty())));
            }
        };
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        super.onOpening(target);
        target.appendJavaScript("Utbetalinger.addKeyNavigation();");
    }

    protected void oppdaterYtelser(List<Record<Hovedytelse>> hovedytelser) {
        filterParametere.setYtelser(hovedytelseToYtelsebeskrivelse(hentHovedytelserFraPeriode(hovedytelser, filterParametere.getStartDato(), filterParametere.getSluttDato())));
        send(getPage(), Broadcast.DEPTH, HOVEDYTELSER_ENDRET);
    }

    protected void oppdaterUtbetalingsvisning(List<Record<Hovedytelse>> synligeUtbetalinger) {
        totalOppsummeringPanel.setDefaultModelObject(new OppsummeringVM(synligeUtbetalinger, filterParametere.getStartDato(), filterParametere.getSluttDato()));
        utbetalingslisteContainer.addOrReplace(createMaanedsPanelListe());
    }

    private void endreSynligeKomponenter(boolean synligeUtbetalinger) {
        if (feilmelding.isVisibilityAllowed()) {
            totalOppsummeringPanel.setVisibilityAllowed(false);
            utbetalingslisteContainer.setVisibilityAllowed(false);
            ingenutbetalinger.setVisibilityAllowed(false);
        } else {
            totalOppsummeringPanel.setVisibilityAllowed(synligeUtbetalinger);
            utbetalingslisteContainer.setVisibilityAllowed(synligeUtbetalinger);
            ingenutbetalinger.setVisibilityAllowed(!synligeUtbetalinger);
        }
    }

    private WebMarkupContainer createUtbetalinglisteContainer() {
        return new WebMarkupContainer("utbetalingslisteContainer");
    }

    private UtbetalingerMessagePanel createFeilmeldingPanel() {
        return new UtbetalingerMessagePanel("feilmelding", FEILMELDING_DEFAULT_KEY, "-ikon-feil");
    }

    private UtbetalingerMessagePanel createIngenUtbetalingerPanel() {
        return new UtbetalingerMessagePanel("ingenutbetalinger", "feil.utbetalinger.ingen-utbetalinger", "-ikon-stjerne");
    }

    private List<Record<Hovedytelse>> hentHovedytelseListe() {
        return getHovedytelseListe(fnr, filterParametere.getStartDato(), filterParametere.getSluttDato());
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FILTER_ENDRET)
    private void oppdaterUtbetalingsliste(AjaxRequestTarget target) {
        feilmelding.setVisibilityAllowed(false);
        DateTime filterStart = filterParametere.getStartDato().toDateTimeAtStartOfDay();
        DateTime filterSlutt = filterParametere.getSluttDato().toDateTimeAtStartOfDay();


        List<Record<Hovedytelse>> hovedytelser = getHovedytelseListe(fnr, filterStart.toLocalDate(), filterSlutt.toLocalDate());
        oppdaterYtelser(hovedytelser);

        List<Record<Hovedytelse>> synligeUtbetalinger = on(hovedytelser).filter(filterParametere).collect();
        oppdaterUtbetalingsvisning(synligeUtbetalinger);
        endreSynligeKomponenter(!synligeUtbetalinger.isEmpty());

        target.add(totalOppsummeringPanel, ingenutbetalinger, feilmelding, utbetalingslisteContainer);
        target.appendJavaScript("Utbetalinger.addKeyNavigation();");
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FEED_ITEM_CLICKED)
    private void ekspanderValgtDetaljPanel(AjaxRequestTarget target, FeedItemPayload payload) {
        filterParametere = new FilterParametere(hovedytelseToYtelsebeskrivelse(hentHovedytelseListe()));
        addOrReplace(createFilterFormPanel());
        oppdaterUtbetalingsliste(target);
        String detaljPanelID = "detaljpanel-" + payload.getItemId();
        target.appendJavaScript("Utbetalinger.haandterDetaljPanelVisning('" + detaljPanelID + "');");
    }
}
