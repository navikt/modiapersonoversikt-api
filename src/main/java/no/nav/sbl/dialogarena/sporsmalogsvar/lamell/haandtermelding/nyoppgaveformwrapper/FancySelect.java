package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

import static java.lang.String.format;

public class FancySelect<T> extends DropDownChoice<T> {
    public static final PackageResourceReference LESS = new PackageResourceReference(FancySelect.class, "FancySelect.less");

    public FancySelect(String id) {
        super(id);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, List<? extends T> choices) {
        super(id, choices);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
        super(id, choices, renderer);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, IModel<T> model, List<? extends T> choices) {
        super(id, model, choices);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, IModel<T> model, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
        super(id, model, choices, renderer);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, IModel<? extends List<? extends T>> choices) {
        super(id, choices);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, IModel<T> model, IModel<? extends List<? extends T>> choices) {
        super(id, model, choices);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
        super(id, choices, renderer);
        this.setOutputMarkupPlaceholderTag(true);
    }

    public FancySelect(String id, IModel<T> model, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
        super(id, model, choices, renderer);
        this.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript(format("$('#%s').combobox()", this.getMarkupId())));
        super.renderHead(response);
    }
}
