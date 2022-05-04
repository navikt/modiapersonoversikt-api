package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.infrastructure.rsbac.*
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import no.nav.modiapersonoversikt.consumer.abac.Decision as AbacDecision

fun AbacResponse.toDecision(denyReason: AbacResponse.() -> String): Decision = when (this.getDecision()) {
    AbacDecision.Deny -> Decision(denyReason(this), DecisionEnums.DENY)
    AbacDecision.Permit -> Decision("", DecisionEnums.PERMIT)
    else -> Decision("", DecisionEnums.NOT_APPLICABLE)
}

class Policies {
    companion object {
        private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")
        private val nasjonalTilgangRoller = setOf("0000-ga-gosys_nasjonal", "0000-ga-gosys_utvidbar_til_nasjonal", "0000-ga-pensjon_nasjonal_u_logg")
        private val regionalTilgangRoller = setOf("0000-ga-gosys_utvidbar_til_nasjonal", "0000-ga-gosys_utvidbar_til_regional", "0000-ga-pensjon_nasjonal_m_logg")
        private val kode6Roller = setOf("0000-ga-strengt_fortrolig_adresse", "0000-ga-gosys_kode6", "0000-ga-pensjon_kode6")
        private val kode7Roller = setOf("0000-ga-fortrolig_adresse", "0000-ga-gosys_kode7", "0000-ga-pensjon_kode7")

        private val abacTilgangTilModiaExperiment = Scientist.createExperiment<Decision>(
            Scientist.Config(
                name = "internal-abac-tilgang-til-modia",
                experimentRate = Scientist.FixedValueRate(0.01)
            )
        )
        private val abacTilgangTilFnrExperiment = Scientist.createExperiment<Decision>(
            Scientist.Config(
                name = "internal-abac-tilgang-til-fnr",
                experimentRate = Scientist.FixedValueRate(0.01)
            )
        )
        private val abacTilgangTilAktoridExperiment = Scientist.createExperiment<Decision>(
            Scientist.Config(
                name = "internal-abac-tilgang-til-aktorid",
                experimentRate = Scientist.FixedValueRate(0.01)
            )
        )
        private fun overrideAbacResultComparator(control: Decision, tryExperiment: Scientist.UtilityClasses.Try<Any?>): Map<String, Any?> {
            if (tryExperiment.isFailure) {
                return emptyMap()
            }

            val controlDecision = control.value
            val experiment = tryExperiment.getOrThrow()
            return when (experiment) {
                is Decision? -> mapOf("ok" to (controlDecision == experiment?.value))
                else -> mapOf("ok" to false, "error" to "Experiment Decision was not of the right type: ${experiment?.javaClass?.simpleName}")
            }
        }

        @JvmField
        val tilgangTilModia = RulePolicy<TilgangskontrollContext> {
            abacTilgangTilModiaExperiment.run(
                control = {
                    checkAbac(AbacPolicies.tilgangTilModia())
                        .toDecision {
                            "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til modia. Årsak: ${getCause()}"
                        }
                },
                experiment = {
                    internalTilgangTilModia(this)
                },
                dataFields = ::overrideAbacResultComparator
            )
        }

        private val internalTilgangTilModia = Policy<TilgangskontrollContext>({ "Saksbehandler (${this.hentSaksbehandlerId()}) har ikke tilgang til modia" }) {
            if (modiaRoller.union(hentSaksbehandlerRoller()).isEmpty()) {
                DecisionEnums.DENY
            } else {
                DecisionEnums.PERMIT
            }
        }

        @JvmField
        val featureToggleEnabled = PolicyGenerator<TilgangskontrollContext, String>({ "Featuretoggle $data is not enabled" }) {
            if (context.featureToggleEnabled(data)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.DENY
            }
        }

        @JvmField
        val tilgangTilBruker = RulePolicyGenerator<TilgangskontrollContext, String> {
            abacTilgangTilFnrExperiment.run(
                control = {
                    context.checkAbac(AbacPolicies.tilgangTilBruker(data))
                        .toDecision {
                            "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data. Årsak: ${getCause()}"
                        }
                },
                experiment = { internalTilgangTilBruker.with(Fnr(data)).invoke(context) },
                dataFields = ::overrideAbacResultComparator
            )
        }

        @JvmField
        val tilgangTilBrukerMedAktorId = RulePolicyGenerator<TilgangskontrollContext, String> {
            abacTilgangTilAktoridExperiment.run(
                control = {
                    context.checkAbac(AbacPolicies.tilgangTilBrukerMedAktorId(data))
                        .toDecision {
                            "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data. Årsak: ${getCause()}"
                        }
                },
                experiment = { internalTilgangTilBruker.with(AktorId(data)).invoke(context) },
                dataFields = ::overrideAbacResultComparator
            )
        }

        private val brukerUtenEnhet = PolicyGenerator<TilgangskontrollContext, EksternBrukerId>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke nasjonal tilgang" }) {
            if (context.hentBrukersEnhet(data) == null) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        private val nasjonalTilgang = PolicyGenerator<TilgangskontrollContext, EksternBrukerId>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke nasjonal tilgang" }) {
            if (nasjonalTilgangRoller.union(context.hentSaksbehandlerRoller()).isNotEmpty()) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        private val tilgangTilLokalKontor = PolicyGenerator<TilgangskontrollContext, EksternBrukerId>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke nasjonal tilgang" }) {
            val brukersEnhet = context.hentBrukersEnhet(data)
            val saksbehandlersEnheter = context.hentSaksbehandlersEnheter()
            if (saksbehandlersEnheter.contains(brukersEnhet)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        private val regionalTilgang = PolicyGenerator<TilgangskontrollContext, EksternBrukerId>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke regional tilgang" }) {
            if (regionalTilgangRoller.union(context.hentSaksbehandlerRoller()).isNotEmpty()) {
                val brukersRegionalEnhet = context.hentBrukersRegionalEnhet(this.data)
                val saksbehandlersEnheter = context.hentSaksbehandlersEnheter()
                if (saksbehandlersEnheter.contains(brukersRegionalEnhet)) {
                    DecisionEnums.PERMIT
                } else {
                    DecisionEnums.NOT_APPLICABLE
                }
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        private val tilgangTilKode6 = PolicyGenerator<TilgangskontrollContext, EksternBrukerId>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kode6 brukere" }) {
            val diskresjonskode: String? = context.hentDiskresjonskode(data)
            if (arrayOf("6", "SPSF").contains(diskresjonskode)) {
                if (kode6Roller.union(context.hentSaksbehandlerRoller()).isNotEmpty()) {
                    DecisionEnums.PERMIT
                } else {
                    DecisionEnums.DENY
                }
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }
        private val tilgangTilKode7 = PolicyGenerator<TilgangskontrollContext, EksternBrukerId>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kode7 brukere" }) {
            val diskresjonskode: String? = context.hentDiskresjonskode(data)
            if (arrayOf("7", "SPFO").contains(diskresjonskode)) {
                if (kode7Roller.union(context.hentSaksbehandlerRoller()).isNotEmpty()) {
                    DecisionEnums.PERMIT
                } else {
                    DecisionEnums.DENY
                }
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        private val denyAlt = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til bruker basert på geografisk tilgang" }) { DecisionEnums.DENY }

        private val geografiskTilgang = PolicySetGenerator(
            combining = CombiningAlgo.firstApplicable,
            policies = listOf(brukerUtenEnhet, nasjonalTilgang, tilgangTilLokalKontor, regionalTilgang, denyAlt.asGenerator())
        )

        private val internalTilgangTilBruker = PolicySetGenerator(
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

        @JvmField
        val behandlingsIderTilhorerBruker = PolicyGenerator<TilgangskontrollContext, BehandlingsIdTilgangData>({ "Ikke alle behandlingsIder tilhørte medsendt fødselsnummer. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            if (context.alleBehandlingsIderTilhorerBruker(data.fnr, data.behandlingsIder)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.DENY
            }
        }

        @JvmField
        val sfDialogTilhorerBruker = PolicyGenerator<TilgangskontrollContext, KjedeIdTilgangData>({ "KjedeId tilhørte ikke bruker. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            DecisionEnums.DENY
        }

        val kanBrukeInternal = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til internal endepunkter" }) {
            hentSaksbehandlerId()
                .map { ident ->
                    val identer = hentSaksbehandlereMedTilgangTilInternal()
                    if (identer.contains(ident)) DecisionEnums.PERMIT else DecisionEnums.DENY
                }.orElse(DecisionEnums.DENY)
        }
    }
}

data class BehandlingsIdTilgangData(val fnr: String, val behandlingsIder: List<String>)
data class KjedeIdTilgangData(val fnr: String, val kjedeId: String)
data class TilgangTilTemaData(val valgtEnhet: String, val tema: String?)

val log: Logger = LoggerFactory.getLogger(Tilgangskontroll::class.java)

open class Tilgangskontroll(context: TilgangskontrollContext) : RSBACImpl<TilgangskontrollContext>(
    context,
    {
        log.error(it)
        ResponseStatusException(HttpStatus.FORBIDDEN, it)
    }
)
