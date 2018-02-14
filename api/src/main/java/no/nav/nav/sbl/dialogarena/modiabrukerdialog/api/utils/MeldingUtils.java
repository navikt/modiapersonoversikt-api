package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.*;

public class MeldingUtils {

    final static Logger logger = LoggerFactory.getLogger(MeldingUtils.class);

    public static Map<String, List<Melding>> skillUtTraader(List<Melding> meldinger) {

        Map<String, List<Melding>> ufiltrertTraaderMap = meldinger.stream().collect(groupingBy(Melding::getTraadId));

        List<Map.Entry<String, List<Melding>>> filtrertEntryList = on(ufiltrertTraaderMap).filter(DET_FINNES_EN_ROTMELDING).collect();

        return lagMap(filtrertEntryList);
    }

    private static Map<String, List<Melding>> lagMap(List<Map.Entry<String, List<Melding>>> entries) {
        HashMap<String, List<Melding>> map = new HashMap<>();
        for (Map.Entry<String, List<Melding>> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private static final Predicate<Map.Entry<String, List<Melding>>> DET_FINNES_EN_ROTMELDING = traad ->
            traad.getValue()
                    .stream()
                    .anyMatch(melding -> melding.id.equals(traad.getKey()));

    public static Transformer<XMLHenvendelse, Melding> tilMelding(final PropertyResolver propertyResolver, final LDAPService ldapService) {
        return xmlHenvendelse -> {
            Melding melding = new Melding();
            melding.id = xmlHenvendelse.getBehandlingsId();
            melding.meldingstype = MELDINGSTYPE_MAP.get(XMLHenvendelseType.fromValue(xmlHenvendelse.getHenvendelseType()));
            melding.opprettetDato = xmlHenvendelse.getOpprettetDato();
            melding.visningsDato = xmlHenvendelse.getOpprettetDato();
            melding.lestDato = xmlHenvendelse.getLestDato();
            melding.fnrBruker = xmlHenvendelse.getFnr();
            melding.traadId = xmlHenvendelse.getBehandlingskjedeId();
            melding.status = STATUS.transform(xmlHenvendelse);
            melding.lestStatus = lagLestStatus(melding);
            melding.statusKlasse = "";
            melding.kontorsperretEnhet = xmlHenvendelse.getKontorsperreEnhet();
            melding.oppgaveId = xmlHenvendelse.getOppgaveIdGsak();
            melding.markertSomFeilsendtAv = xmlHenvendelse.getMarkertSomFeilsendtAv();
            melding.eksternAktor = xmlHenvendelse.getEksternAktor();
            melding.tilknyttetEnhet = xmlHenvendelse.getTilknyttetEnhet();
            melding.brukersEnhet = xmlHenvendelse.getBrukersEnhet();
            melding.erTilknyttetAnsatt = xmlHenvendelse.isErTilknyttetAnsatt();
            melding.gjeldendeTemagruppe = xmlHenvendelse.getGjeldendeTemagruppe() != null && !"".equals(xmlHenvendelse.getGjeldendeTemagruppe())
                    ? Temagruppe.valueOf(xmlHenvendelse.getGjeldendeTemagruppe()) : null;
            melding.erFerdigstiltUtenSvar = unboxBoolean(xmlHenvendelse.isFerdigstiltUtenSvar());

            oppdaterMeldingMedJournalfortInformasjon(propertyResolver, ldapService, xmlHenvendelse, melding);

            if (innholdErKassert(xmlHenvendelse)) {
                oppdaterMeldingMedKasseringData(propertyResolver, melding);
                return melding;
            }

            XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
            if (DOKUMENT_VARSEL.name().equals(xmlHenvendelse.getHenvendelseType())) {
                oppdaterMeldingMedDokumentVarselData(propertyResolver, xmlHenvendelse, melding, xmlMetadata);
                return melding;
            }

            if (OPPGAVE_VARSEL.name().equals(xmlHenvendelse.getHenvendelseType())) {
                oppdaterMeldingMedOppgaveVarselData(propertyResolver, xmlHenvendelse, melding, xmlMetadata);
                return melding;
            }

            melding.statusTekst = getTekstForMeldingStatus(propertyResolver, melding.meldingstype);
            if (xmlMetadata instanceof XMLMeldingFraBruker) {
                XMLMeldingFraBruker meldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
                settTemagruppe(melding, meldingFraBruker.getTemagruppe(), propertyResolver);
                melding.withFritekst(new Fritekst(meldingFraBruker.getFritekst(), melding.skrevetAv, xmlHenvendelse.getOpprettetDato()));
            } else if (xmlMetadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
                settTemagruppe(melding, meldingTilBruker.getTemagruppe(), propertyResolver);
                melding.kanal = meldingTilBruker.getKanal();
                Saksbehandler skrevetAv = ldapService.hentSaksbehandler(meldingTilBruker.getNavident());
                melding.skrevetAv = skrevetAv;
                melding.navIdent = meldingTilBruker.getNavident();
                melding.withFritekst(new Fritekst(meldingTilBruker.getFritekst(), skrevetAv, xmlHenvendelse.getOpprettetDato()));
            } else {
                throw new RuntimeException("XMLMetadata er av en ukjent type: " + xmlMetadata);
            }

            return melding;
        };
    }

    private static boolean unboxBoolean(Boolean bool) {
        if (bool != null) return bool;
        return false;
    }

    private static void oppdaterMeldingMedJournalfortInformasjon(final PropertyResolver propertyResolver, final LDAPService ldapService, final XMLHenvendelse xmlHenvendelse, final Melding melding) {
        XMLJournalfortInformasjon journalfortInformasjon = xmlHenvendelse.getJournalfortInformasjon();
        if (journalfortInformasjon != null) {
            melding.statusTekst = getTekstForMeldingStatus(propertyResolver, melding.meldingstype);
            melding.journalfortDato = journalfortInformasjon.getJournalfortDato();
            melding.journalfortTema = journalfortInformasjon.getJournalfortTema();
            melding.journalfortSaksId = journalfortInformasjon.getJournalfortSaksId();
            melding.journalfortAvNavIdent = journalfortInformasjon.getJournalforerNavIdent();
            melding.journalfortAv = ldapService.hentSaksbehandler(journalfortInformasjon.getJournalforerNavIdent());
        }
    }

    private static void oppdaterMeldingMedKasseringData(final PropertyResolver propertyResolver, final Melding melding) {
        settTemagruppe(melding, null, propertyResolver);
        melding.statusTekst = getTekstForMeldingStatus(propertyResolver, melding.meldingstype);
        melding.withFritekst(new Fritekst(propertyResolver.getProperty("innhold.kassert"), melding.skrevetAv, melding.opprettetDato));
        melding.kanal = null;
        melding.kassert = true;
    }

    private static void oppdaterMeldingMedDokumentVarselData(final PropertyResolver propertyResolver, final XMLHenvendelse xmlHenvendelse, final Melding melding, final XMLMetadata xmlMetadata) {
        XMLDokumentVarsel dokumentVarsel = (XMLDokumentVarsel) xmlMetadata;
        melding.statusTekst = dokumentVarsel.getDokumenttittel();
        melding.withFritekst(new Fritekst(format(propertyResolver.getProperty("dokument.fritekst"), dokumentVarsel.getTemanavn()), melding.skrevetAv, melding.opprettetDato));
        melding.erDokumentMelding = true;
        melding.withTraadId(xmlHenvendelse.getBehandlingsId());
        melding.lestStatus = lagLestStatusDokumentVarsel(melding);
        melding.ferdigstiltDato = dokumentVarsel.getFerdigstiltDato();
        melding.visningsDato = dokumentVarsel.getFerdigstiltDato();
        melding.statusKlasse = "dokument";
    }

    private static void oppdaterMeldingMedOppgaveVarselData(final PropertyResolver propertyResolver, final XMLHenvendelse xmlHenvendelse, final Melding melding, final XMLMetadata xmlMetadata) {
        XMLOppgaveVarsel oppgaveVarsel = (XMLOppgaveVarsel) xmlMetadata;
        melding.statusTekst = hentEnonicTekstDynamic(propertyResolver, format("oppgave.%s", oppgaveVarsel.getOppgaveType()), "oppgave.GEN");
        melding.withFritekst(new Fritekst(hentEnonicTekstDynamic(propertyResolver, format("oppgave.%s.fritekst", oppgaveVarsel.getOppgaveType()), "oppgave.GEN.fritekst"), melding.skrevetAv, melding.opprettetDato));
        melding.erOppgaveMelding = true;
        melding.traadId = xmlHenvendelse.getBehandlingsId();
        melding.lestStatus = lagLestStatusDokumentVarsel(melding);
        melding.statusKlasse = "oppgave";
    }

    static String getTekstForMeldingStatus(PropertyResolver propertyResolver, Meldingstype meldingstype) {
        return hentEnonicTekstForMeldingStatus(propertyResolver, meldingstype)
                .orElse(getTekstForMeldingStatus(meldingstype)
                        .orElse(lagMeldingStatusTekstKey(meldingstype)));
    }

    static Optional<String> hentEnonicTekstForMeldingStatus(PropertyResolver propertyResolver, Meldingstype meldingstype) {
        if(!skalHenteStatusTekstForMelding(meldingstype)) {
            return Optional.empty();
        }

        String key = lagMeldingStatusTekstKey(meldingstype);
        try {
            return Optional.ofNullable(propertyResolver.getProperty(key));
        } catch(NoSuchElementException exception) {
            logger.error("Finner ikke cms-oppslag for " + key, exception.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<String> getTekstForMeldingStatus(Meldingstype meldingstype) {
        if (meldingstype.equals(DELVIS_SVAR_SKRIFTLIG)) {
            return Optional.of("Delsvar – Ikke sendt");
        }
        return Optional.empty();
    }

    private static boolean skalHenteStatusTekstForMelding(Meldingstype meldingstype) {
        return meldingstype != DELVIS_SVAR_SKRIFTLIG;
    }

    protected static String hentEnonicTekstDynamic(PropertyResolver resolver, String key, String defaultKey) {
        try {
            return resolver.getProperty(key);
        } catch (Exception e) {
            return resolver.getProperty(defaultKey);
        }
    }

    private static String lagLestStatus(Melding melding) {
        if (VisningUtils.FRA_BRUKER.contains(melding.meldingstype)) {
            return "";
        } else if (melding.status == Status.LEST_AV_BRUKER) {
            return "Lest,";
        } else {
            return "Ulest,";
        }
    }

    private static String lagLestStatusDokumentVarsel(Melding melding) {
        if (melding.status == Status.LEST_AV_BRUKER) {
            return "Lest";
        }
        return "Ulest";
    }

    private static void settTemagruppe(Melding melding, String temagruppe, PropertyResolver propertyResolver) {
        melding.temagruppe = temagruppe;
        if (temagruppe == null) {
            melding.temagruppeNavn = propertyResolver.getProperty("temagruppe.kassert");
        } else {
            melding.temagruppeNavn = propertyResolver.getProperty(temagruppe);
        }
    }

    public static boolean innholdErKassert(XMLHenvendelse xmlHenvendelse) {
        return xmlHenvendelse.getMetadataListe() == null;
    }

    public static final Transformer<XMLHenvendelse, Status> STATUS = new Transformer<XMLHenvendelse, Status>() {
        @Override
        public Status transform(XMLHenvendelse info) {
            Meldingstype meldingstype = MELDINGSTYPE_MAP.get(XMLHenvendelseType.fromValue(info.getHenvendelseType()));

            if (FRA_BRUKER.contains(meldingstype)) {
                return IKKE_BESVART;
            } else if (FRA_NAV.contains(meldingstype)) {
                DateTime lestDato = info.getLestDato();
                if (lestDato != null) {
                    return LEST_AV_BRUKER;
                } else {
                    return IKKE_LEST_AV_BRUKER;
                }
            } else {
                throw new RuntimeException("Ukjent henvendelsestype: " + meldingstype);
            }
        }
    };

    public static final Map<XMLHenvendelseType, Meldingstype> MELDINGSTYPE_MAP = new HashMap<XMLHenvendelseType, Meldingstype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, SVAR_TELEFON);
            put(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG, DELVIS_SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, SAMTALEREFERAT_TELEFON);
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, SPORSMAL_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, SVAR_SBL_INNGAAENDE);
            put(XMLHenvendelseType.DOKUMENT_VARSEL, DOKUMENT_VARSEL);
            put(XMLHenvendelseType.OPPGAVE_VARSEL, OPPGAVE_VARSEL);
        }
    };
}
