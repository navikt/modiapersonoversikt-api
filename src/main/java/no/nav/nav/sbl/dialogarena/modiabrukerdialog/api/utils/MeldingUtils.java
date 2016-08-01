package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.TRAAD_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.*;

public class MeldingUtils {

    final static Logger logger = LoggerFactory.getLogger(MeldingUtils.class);

    public static Map<String, List<Melding>> skillUtTraader(List<Melding> meldinger) {

        Map<String, List<Melding>> ufiltrertTraaderMap = on(meldinger).reduce(indexBy(TRAAD_ID), new HashMap<>());

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

    private static final Predicate<Map.Entry<String, List<Melding>>> DET_FINNES_EN_ROTMELDING = traad -> on(traad.getValue()).exists(where(ID, equalTo(traad.getKey())));

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
            melding.statusKlasse = VisningUtils.lagStatusKlasse(melding);
            melding.kontorsperretEnhet = xmlHenvendelse.getKontorsperreEnhet();
            melding.oppgaveId = xmlHenvendelse.getOppgaveIdGsak();
            melding.markertSomFeilsendtAv = xmlHenvendelse.getMarkertSomFeilsendtAv();
            melding.eksternAktor = xmlHenvendelse.getEksternAktor();
            melding.tilknyttetEnhet = xmlHenvendelse.getTilknyttetEnhet();
            melding.brukersEnhet = xmlHenvendelse.getBrukersEnhet();
            melding.erTilknyttetAnsatt = xmlHenvendelse.isErTilknyttetAnsatt();
            melding.gjeldendeTemagruppe = xmlHenvendelse.getGjeldendeTemagruppe() != null && !"".equals(xmlHenvendelse.getGjeldendeTemagruppe()) ? Temagruppe.valueOf(xmlHenvendelse.getGjeldendeTemagruppe()) : null;

            XMLJournalfortInformasjon journalfortInformasjon = xmlHenvendelse.getJournalfortInformasjon();
            if (journalfortInformasjon != null) {
                melding.statusTekst = hentEnonicTekstForDynamiskNokkel(propertyResolver, lagMeldingStatusTekstKey(melding));
                melding.journalfortDato = journalfortInformasjon.getJournalfortDato();
                melding.journalfortTema = journalfortInformasjon.getJournalfortTema();
                melding.journalfortSaksId = journalfortInformasjon.getJournalfortSaksId();
                melding.journalfortAvNavIdent = journalfortInformasjon.getJournalforerNavIdent();
                melding.journalfortAv = ldapService.hentSaksbehandler(journalfortInformasjon.getJournalforerNavIdent());
            }

            if (innholdErKassert(xmlHenvendelse)) {
                settTemagruppe(melding, null, propertyResolver);
                melding.statusTekst = hentEnonicTekstForDynamiskNokkel(propertyResolver, lagMeldingStatusTekstKey(melding));
                melding.fritekst = propertyResolver.getProperty("innhold.kassert");
                melding.kanal = null;
                melding.navIdent = null;
                melding.kassert = true;
                return melding;
            }

            XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
            if(DOKUMENT_VARSEL.name().equals(xmlHenvendelse.getHenvendelseType())) {
                XMLDokumentVarsel dokumentVarsel = (XMLDokumentVarsel) xmlMetadata;
                melding.statusTekst = dokumentVarsel.getDokumenttittel();
                melding.fritekst = dokumentVarsel.getTemanavn();
                melding.erDokumentMelding = true;
                melding.withTraadId(xmlHenvendelse.getBehandlingsId());
                melding.lestStatus = lagLestStatusDokumentVarsel(melding);
                melding.ferdigstiltDato = dokumentVarsel.getFerdigstiltDato();
                melding.visningsDato = dokumentVarsel.getFerdigstiltDato();
                melding.statusKlasse = "dokument";
                return melding;
            }

            melding.statusTekst = hentEnonicTekstForDynamiskNokkel(propertyResolver, lagMeldingStatusTekstKey(melding));
            if (xmlMetadata instanceof XMLMeldingFraBruker) {
                XMLMeldingFraBruker meldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
                settTemagruppe(melding, meldingFraBruker.getTemagruppe(), propertyResolver);
                melding.fritekst = meldingFraBruker.getFritekst();
            } else if (xmlMetadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
                settTemagruppe(melding, meldingTilBruker.getTemagruppe(), propertyResolver);
                melding.fritekst = meldingTilBruker.getFritekst();
                melding.kanal = meldingTilBruker.getKanal();
                melding.navIdent = meldingTilBruker.getNavident();
                melding.skrevetAv = ldapService.hentSaksbehandler(meldingTilBruker.getNavident());
            } else {
                throw new RuntimeException("XMLMetadata er av en ukjent type: " + xmlMetadata);
            }

            return melding;
        };
    }

    private static String hentEnonicTekstForDynamiskNokkel(PropertyResolver propertyResolver, String key) {
        try {
            String result = propertyResolver.getProperty(key);
            return result;
        } catch(NoSuchElementException exception) {
            logger.error("Finner ikke cms-oppslag for " + key, exception.getMessage());
            return key;
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
            put(XMLHenvendelseType.REFERAT_OPPMOTE, SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, SAMTALEREFERAT_TELEFON);
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, SPORSMAL_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, SVAR_SBL_INNGAAENDE);
            put(XMLHenvendelseType.DOKUMENT_VARSEL, DOKUMENT_VARSEL);
        }
    };
}
