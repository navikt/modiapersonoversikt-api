package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.sendsporsmal;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

public class SporsmalBekreftelsePanel extends Panel {
	
    public SporsmalBekreftelsePanel(String id, final CompoundPropertyModel<Sporsmal> model, final SideNavigerer nesteSide) {
        super(id);
        add(new Label("tidspunkt", new Model<String>() {
        	@Override
        	public String getObject() {
        		DateTime tidspunkt = model.getObject().innsendingsTidspunkt;
        		return "kl " + tidspunkt.toString("hh:mm") + " den " + tidspunkt.toString("dd. MMMM YYYY", Session.get().getLocale());
        	}
        }));
        add(new AjaxLink<Void>("til-mine-henvendelser") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                nesteSide.forside();
                target.add(SporsmalBekreftelsePanel.this.getParent());
            }
        });
    }
}
