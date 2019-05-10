package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.brukerdialog.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.brukerdialog.security.tilgangskontroll.policy.request.attributes.PolicyAttribute;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.VARSEL;
import static org.apache.commons.lang3.StringUtils.defaultString;


public class TraadVM implements Serializable {


    private List<MeldingVM> meldinger;

    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public Sak journalfortSak;

    private EnforcementPoint pep;

    public TraadVM(List<MeldingVM> meldinger, EnforcementPoint pep, SaksbehandlerInnstillingerService saksbehandlerInnstillingerService) {
        this.meldinger = meldinger;
        this.pep = pep;
        this.saksbehandlerInnstillingerService = saksbehandlerInnstillingerService;
    }

    public List<MeldingVM> getMeldinger() {
        return meldinger;
    }

    public MeldingVM getNyesteMelding() {
        if (meldinger.isEmpty()) {
            return null;
        }
        return meldinger.get(0);
    }

    public MeldingVM getEldsteMelding() {
        if (meldinger.isEmpty()) {
            return null;
        }
        return meldinger.get(meldinger.size() - 1);
    }

    public List<MeldingVM> getTidligereMeldinger() {
        return meldinger.isEmpty() ? new ArrayList<>() : meldinger.subList(1, meldinger.size());
    }

    public String getNyesteMeldingsTemagruppe() {
        return getNyesteMelding().melding.temagruppe;
    }

    public int getTraadLengde() {
        return meldinger.size();
    }

    public boolean erBehandlet() {
        return minstEnMeldingErFraNav() || erFerdigstiltUtenSvar();
    }

    private boolean minstEnMeldingErFraNav() {
        return meldinger.stream()
                .filter(melding -> !melding.erDelsvar())
                .anyMatch((meldingVM) -> meldingVM.melding.erFraSaksbehandler());
    }

    public boolean erKontorsperret() {
        return getKontorsperretEnhet().isPresent();
    }

    public Optional<String> getKontorsperretEnhet() {
        if (meldinger.isEmpty()) {
            return Optional.empty();
        }

        return ofNullable(getEldsteMelding().melding.kontorsperretEnhet);
    }

    public boolean erFeilsendt() {
        return meldinger.stream().anyMatch(MeldingVM::erFeilsendt);
    }

    public boolean traadKanBesvares() {
        return getEldsteMelding().erSporsmal()
                && !getEldsteMelding().melding.kassert
                && (erEnkeltstaaendeSpsmFraBruker() || !getEldsteMelding().erKontorsperret())
                && !getEldsteMelding().erFeilsendt()
                && !(getEldsteMelding().melding.gjeldendeTemagruppe == Temagruppe.OKSOS && !traadOKSOSKanSes())
                && !getEldsteMelding().melding.ingenTilgangJournalfort
                && !erFerdigstiltUtenSvar();
    }

    public boolean erMonolog() {
        return meldinger.stream()
                .map(MeldingVM::erFraSaksbehandler)
                .distinct()
                .count()
                < 2;
    }

    public boolean erVarsel() {
        return VARSEL.contains(getEldsteMelding().getMeldingstype());
    }

    public boolean erTemagruppeSosialeTjenester() {
        Temagruppe gjeldendeTemagruppe = getEldsteMelding().melding.gjeldendeTemagruppe;
        return asList(Temagruppe.OKSOS, Temagruppe.ANSOS).contains(gjeldendeTemagruppe);
    }

    private boolean traadOKSOSKanSes() {
        String valgtEnhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();

        List<PolicyAttribute> attributes = new ArrayList<>(Arrays.asList(
                actionId("oksos"),
                resourceId(""),
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:bruker-enhet", defaultString(getEldsteMelding().melding.brukersEnhet))
        ));

        PolicyRequest okonomiskSosialhjelpPolicyRequest = forRequest(attributes);
        return pep.hasAccess(okonomiskSosialhjelpPolicyRequest);
    }

    public boolean erJournalfort() {
        return getEldsteMelding().isJournalfort();
    }

    public boolean erFerdigstiltUtenSvar() {
        return getEldsteMelding().erFerdigstiltUtenSvar();
    }

    private boolean erEnkeltstaaendeSpsmFraBruker() {
        return meldinger.size() == 1 && erMeldingstypeSporsmal();
    }

    public boolean erMeldingstypeSporsmal() {
        return getEldsteMelding().getMeldingstype() == Meldingstype.SPORSMAL_SKRIFTLIG
                || getEldsteMelding().getMeldingstype() == Meldingstype.SPORSMAL_SKRIFTLIG_DIREKTE;
    }

    public boolean erSisteMeldingEtDelsvar() {
        return getNyesteMelding().erDelsvar();
    }

    public boolean harDelsvar() {
        return meldinger.stream().anyMatch(MeldingVM::erDelsvar);
    }

    public Optional<DateTime> getFerdigstiltUtenSvarDato() {
        return getEldsteMelding().getFerdigstiltUtenSvarDato();
    }

    public Optional<Saksbehandler> getFerdigstiltUtenSvarAv() {
        return getEldsteMelding().getFerdigstiltUtenSvarAv();
    }

    public Optional<DateTime> getKontorsperretDato() {
        return getEldsteMelding().getKontorsperretDato();
    }

    public Optional<Saksbehandler> getKontorsperretAv() {
        return getEldsteMelding().getKontorsperretAv();
    }

}
