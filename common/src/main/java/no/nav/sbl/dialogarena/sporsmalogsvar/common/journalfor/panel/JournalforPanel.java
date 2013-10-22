package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.panel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Arkivtema;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Sak;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.DateTime;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class JournalforPanel extends Panel {

    public static final PackageResourceReference LESS_REFERENCE = new PackageResourceReference(JournalforPanel.class, "journalfor.less");

    public JournalforPanel(String id) {
        super(id);

        final JournalforForm journalforForm = new JournalforForm("journalfor-form");
        journalforForm.setVisibilityAllowed(false);
        journalforForm.setOutputMarkupPlaceholderTag(true);

        AjaxLink<Void> startJournalforing = new AjaxLink<Void>("start-journalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                journalforForm.setVisibilityAllowed(true);
                target.add(journalforForm);
            }
        };
        add(startJournalforing, journalforForm);
    }

    private class JournalforForm extends Form<Sak> {

        public JournalforForm(String id) {
            super(id);
            List<Sak> saker = asList(
                    new Sak("1", "Generell", "GSAK", Arkivtema.BID, DateTime.now().minusDays(2)),
                    new Sak("2", "Generell", "GSAK", Arkivtema.BID, DateTime.now().minusHours(5)),
                    new Sak("3", "Generell", "PSYS", Arkivtema.PEN, DateTime.now().minusMonths(1)),
                    new Sak("4", "Generell", "GSAK", Arkivtema.SER, DateTime.now().minusWeeks(4)),
                    new Sak("5", "Generell", "PSYS", Arkivtema.UFO, DateTime.now().minusDays(2)),
                    new Sak("6", "Generell", "GSAK", Arkivtema.VEN, DateTime.now().minusWeeks(2)));

            final Map<String, List<Sak>> sakerPerTema = new HashMap<>();
            for (Arkivtema arkivtema : on(saker).map(tilArkivtema).collectIn(new HashSet<Arkivtema>())) {
                sakerPerTema.put(arkivtema.toString(), on(saker).filter(where(tilArkivtema, equalTo(arkivtema))).collect(sorterNyesteOverst));
            }
            final RadioGroup<Sak> sakRadioGroup = new RadioGroup<>("sak-radio-group", new Model<Sak>());
            sakRadioGroup.add(new ListView<String>("saker-per-tema", on(sakerPerTema.keySet()).collect(String.CASE_INSENSITIVE_ORDER)) {
                @Override
                protected void populateItem(ListItem<String> item) {
                    item.add(new Label("tema", item.getModelObject()));
                    item.add(new ListView<Sak>("sak", sakerPerTema.get(item.getModelObject())) {
                        @Override
                        protected void populateItem(ListItem<Sak> item) {
                            Radio<Sak> radio = new Radio<>("radio", item.getModel());
                            item.add(radio);
                            WebMarkupContainer sakContainer = new WebMarkupContainer("sak-container");
                            sakContainer.add(new AttributeModifier("for", radio.getMarkupId()));
                            Sak sak = item.getModelObject();
                            sakContainer.add(new Label("opprettet-dato", sak.opprettetDato));
                            sakContainer.add(new Label("fagsystem", sak.fagsystem));
                            sakContainer.add(new AttributeModifier("for", radio.getMarkupId()));
                            item.add(sakContainer);
                        }
                    });
                }
            });
            final RadioGroup<Boolean> sensitivRadioGroup = new RadioGroup<>("sensitiv-radio-group", new Model<Boolean>());
            sensitivRadioGroup.add(new Radio<>("ikke-sensitivt", Model.of(false)));
            sensitivRadioGroup.add(new Radio<>("sensitivt", Model.of(true)));

            AjaxSubmitLink journalfor = new AjaxSubmitLink("journalfor-submit") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    // TODO: Sende tråden over til arkivering tilknyttet saken som er valgt
                }
            };

            add(sakRadioGroup, sensitivRadioGroup, journalfor);
        }


        private final Transformer<Sak, Arkivtema> tilArkivtema = new Transformer<Sak, Arkivtema>() {
            @Override
            public Arkivtema transform(Sak sak) {
                return sak.arkivtema;
            }
        };

        private final Comparator<Sak> sorterNyesteOverst = new Comparator<Sak>() {
            @Override
            public int compare(Sak o1, Sak o2) {
                return o2.opprettetDato.compareTo(o1.opprettetDato);
            }
        };
    }

}
