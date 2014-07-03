package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public class SakerRadioGroup extends RadioGroup<Sak> {
    private final static String FAGSAK_PROPERTY_NAVN = "journalfor.sakstype.tekst.fagsak";
    private final static String GENERELL_PROPERTY_NAVN = "journalfor.sakstype.tekst.generell";

    public SakerRadioGroup(String id, SakerVM sakerVM) {
        super(id);
        setRequired(true);

        add(
                new SakerPerSakstypeRadioChoices("sakstypePanelFagsaker", new PropertyModel<List<TemaMedSaker>>(sakerVM, "fagsakerGruppertPaaTema"), FAGSAK_PROPERTY_NAVN),
                new SakerPerSakstypeRadioChoices("sakstypePanelGenerelle", new PropertyModel<List<TemaMedSaker>>(sakerVM, "generelleSakerGruppertPaaTema"), GENERELL_PROPERTY_NAVN)
        );

    }

}
