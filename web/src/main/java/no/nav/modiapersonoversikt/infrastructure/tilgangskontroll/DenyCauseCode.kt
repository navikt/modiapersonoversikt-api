package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.personoversikt.common.kabac.Decision

enum class DenyCauseCode : Decision.DenyCause {
    FP1_KODE6,
    FP2_KODE7,
    FP3_EGEN_ANSATT,
    FP4_GEOGRAFISK,
    AD_ROLLE,
    INGEN_ENHETER,
    UNKNOWN;
}
