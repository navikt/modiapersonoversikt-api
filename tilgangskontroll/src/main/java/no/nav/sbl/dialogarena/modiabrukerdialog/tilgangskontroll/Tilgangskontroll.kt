package no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll

import no.nav.sbl.dialogarena.rsbac.*
import org.slf4j.LoggerFactory
import javax.ws.rs.ForbiddenException

private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")
private val pesysRoller = setOf(
        "0000-GA-PENSJON_SAKSBEHANDLER",
        "0000-GA-Pensjon_VEILEDER",
        "0000-GA-Pensjon_BEGRENSET_VEILEDER",
        "0000-GA-PENSJON_BRUKERHJELPA",
        "0000-GA-PENSJON_SAKSBEHANDLER",
        "0000-GA-PENSJON_KLAGEBEH",
        "0000-GA-Pensjon_Okonomi"
)

class Policies {
    companion object {
        @JvmField
        val tilgangTilModia = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til modia" }) {
            if (modiaRoller.any { rolle -> harSaksbehandlerRolle(rolle) })
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val tilgangTilKode6 = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kode6 brukere" }) {
            if (context.hentDiskresjonkode(data) == "6") {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE6"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val tilgangTilKode7 = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kode7 brukere" }) {
            if (context.hentDiskresjonkode(data) == "7") {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE7"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        @JvmField
        val tilgangTilDiskresjonskode = PolicyGenerator<TilgangskontrollContextUtenTPS, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data" }) {
            val diskresjonskode = data
            if (arrayOf("6", "SPSF").contains(diskresjonskode)) {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE6"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else if (arrayOf("7", "SPFO").contains(diskresjonskode)) {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE7"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }
        val brukerUtenEnhet = PolicyGenerator<TilgangskontrollContext, String>({ "Bruker har ingen enhet" }) {
            if (context.hentBrukersEnhet(data).isNullOrBlank()) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val tilgangTilLokalKontorGittFnr = PolicyGenerator<TilgangskontrollContext, String>({ "" }) {
            val brukersEnhet = context.hentBrukersEnhet(data)
            if (context.hentSaksbehandlerLokalEnheter().contains(brukersEnhet)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        @JvmField
        val tilgangTilEnhetId = PolicyGenerator<TilgangskontrollContextUtenTPS, String>({ "" }) {
            if (context.hentSaksbehandlerLokalEnheter().contains(data)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val nasjonalTilgang = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har nasjonal tilgang" }) {
            if (context.harSaksbehandlerRolle("0000-GA-GOSYS_NASJONAL") || context.harSaksbehandlerRolle("0000-GA-GOSYS_UTVIDBAR_TIL_NASJONAL")) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val regionalTilgang = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har regional tilgang" }) {
            val brukersEnhet = context.hentBrukersEnhet(data)
            val harRegionalRolle = context.harSaksbehandlerRolle("0000-GA-GOSYS_REGIONAL") || context.harSaksbehandlerRolle("0000-GA-GOSYS_UTVIDBAR_TIL_REGIONAL")

            if (harRegionalRolle && context.hentSaksbehandlersFylkesEnheter().contains(brukersEnhet)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val denyAlt = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til bruker basert på geografisk tilgang" }) { DecisionEnums.DENY }
        val geografiskTilgang = PolicySetGenerator(
                combining = CombiningAlgo.firstApplicable,
                policies = listOf(brukerUtenEnhet, nasjonalTilgang, tilgangTilLokalKontorGittFnr, regionalTilgang, denyAlt.asGenerator())
        )

        @JvmField
        val tilgangTilBruker = PolicySetGenerator(
                policies = listOf(tilgangTilModia.asGenerator(), geografiskTilgang, tilgangTilKode6, tilgangTilKode7)
        )

        @JvmField
        val tilgangTilTema = PolicyGenerator<TilgangskontrollContext, TilgangTilTemaData>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til tema: ${data.tema} enhet: ${data.valgtEnhet}" }) {
            val temaer = context.hentTemagrupperForSaksbehandler(data.valgtEnhet)
            if (temaer.contains(data.tema)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.DENY
            }
        }

        val tilgangTilAareg = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til aareg" }) {
            if (harSaksbehandlerRolle("0000-GA-Aa-register-Lese")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val kanEndreAdresse = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til å endre adresse" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_EndreKontaktAdresse")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val kanEndreKontonummer = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til å endre bankkontonummer" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_EndreKontonummer")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val kanEndreNavn = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til å endre navn" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_EndreNavn")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        @JvmField
        val tilgangTilKontorsperretMelding = PolicyGenerator<TilgangskontrollContext, TilgangTilKontorSperreData>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kontorsperret melding" }) {
            when {
                data.ansvarligEnhet == null || data.ansvarligEnhet.isEmpty() -> DecisionEnums.PERMIT
                data.valgtEnhet == data.ansvarligEnhet -> DecisionEnums.PERMIT
                else -> DecisionEnums.DENY
            }
        }

        @JvmField
        val tilgangTilOksosMelding = PolicyGenerator<TilgangskontrollContext, TilgangTilOksosSperreData>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til økonomisk sosialhjelp" }) {
            when {
                context.harSaksbehandlerRolle("0000-GA-Okonomisk_Sosialhjelp") -> DecisionEnums.PERMIT
                data.valgtEnhet == data.brukersEnhet -> DecisionEnums.PERMIT
                else -> DecisionEnums.DENY
            }
        }

        val tilgangTilOppfoling = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til oppfølging" }) {
            if (harSaksbehandlerRolle("0000-GA-Modia-Oppfolging")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val tilgangTilPesys = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til pesys" }) {
            if (pesysRoller.any { rolle -> harSaksbehandlerRolle(rolle) })
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val kanPlukkeOppgave = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til plukk oppgave" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_HentOppgave")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        @JvmField
        val behandlingsIderTilhorerBruker = PolicyGenerator<TilgangskontrollContext, BehandlingsIdTilgangData>({ "Ikke alle behandlingsIder tilhørte medsendt fødselsnummer. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            if (context.alleBehandlingsIderTilhorerBruker(data.fnr, data.behandlingsIder))
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val kanHastekassere = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til hastekassering" }) {
            hentSaksbehandlerId()
                    .map { ident ->
                        val identer = hentSaksbehandlereMedTilgangTilHastekassering()
                        if (identer.contains(ident)) DecisionEnums.PERMIT else DecisionEnums.DENY
                    }.orElse(DecisionEnums.DENY)
        }
    }
}

data class TilgangTilKontorSperreData(val valgtEnhet: String, val ansvarligEnhet: String?)
data class TilgangTilOksosSperreData(val valgtEnhet: String, val brukersEnhet: String)
data class BehandlingsIdTilgangData(val fnr: String, val behandlingsIder: List<String>)
data class TilgangTilTemaData(val valgtEnhet: String, val tema: String)

val log = LoggerFactory.getLogger(Tilgangskontroll::class.java)

class TilgangskontrollUtenTPS(context: TilgangskontrollContextUtenTPS) : RSBACImpl<TilgangskontrollContextUtenTPS>(context, {
    log.error(it)
    ForbiddenException(it)
})

class Tilgangskontroll(context: TilgangskontrollContext) : RSBACImpl<TilgangskontrollContext>(context, {
    log.error(it)
    ForbiddenException(it)
})
