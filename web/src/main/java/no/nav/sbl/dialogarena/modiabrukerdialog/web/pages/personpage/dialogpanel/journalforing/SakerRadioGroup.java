package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.SakerForTema;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public class SakerRadioGroup extends RadioGroup<Sak> {
    private static final String FAGSAK_PROPERTY_NAVN = "journalfor.sakstype.tekst.fagsak";
    private static final String GENERELL_PROPERTY_NAVN = "journalfor.sakstype.tekst.generell";
    private static final String SOSIALE_PROPERTY_NAVN = "journalfor.sakstype.tekst.sosiale";

    public SakerRadioGroup(String id, SakerVM sakerVM, boolean visSosialeTjenester) {
        super(id);
        setRequired(true);

        add(
                new SakerPerSakstypeRadioChoices(
                        "sakstypePanelFagsaker",
                        new PropertyModel<List<SakerForTema>>(sakerVM, "fagsakerGruppertPaaTema"),
                        FAGSAK_PROPERTY_NAVN,
                        sakerVM.visFagsaker
                ),
                new SakerPerSakstypeRadioChoices(
                        "sakstypePanelGenerelle",
                        new PropertyModel<List<SakerForTema>>(sakerVM, "generelleSakerGruppertPaaTema"),
                        GENERELL_PROPERTY_NAVN,
                        sakerVM.visGenerelleSaker
                ),
                new SosialeTjenesterRadioChoices(
                        "sakstypePanelSosialeTjenester",
                        SOSIALE_PROPERTY_NAVN,
                        sakerVM.visSosialeTjenester).setVisibilityAllowed(visSosialeTjenester)
        );
    }

}
