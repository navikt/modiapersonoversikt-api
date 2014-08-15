package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public class SakerRadioGroup extends RadioGroup<Sak> {
    private static final String FAGSAK_PROPERTY_NAVN = "journalfor.sakstype.tekst.fagsak";
    private static final String GENERELL_PROPERTY_NAVN = "journalfor.sakstype.tekst.generell";

    public SakerRadioGroup(String id, SakerVM sakerVM) {
        super(id);
        setRequired(true);

        add(
                new SakerPerSakstypeRadioChoices("sakstypePanelFagsaker", new PropertyModel<List<TemaSaker>>(sakerVM, "fagsakerGruppertPaaTema"), FAGSAK_PROPERTY_NAVN),
                new SakerPerSakstypeRadioChoices("sakstypePanelGenerelle", new PropertyModel<List<TemaSaker>>(sakerVM, "generelleSakerGruppertPaaTema"), GENERELL_PROPERTY_NAVN)
        );

    }

}
