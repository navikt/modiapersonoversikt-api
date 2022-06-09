package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.modiapersonoversikt.infrastructure.kabac.Decision

enum class DenyCauseCode : Decision.DenyCause {
    FP1_KODE6,
    FP2_KODE7,
    FP3_EGEN_ANSATT,
    FP4_GEOGRAFISK,
    AD_ROLLE,
    UNKNOWN;
}