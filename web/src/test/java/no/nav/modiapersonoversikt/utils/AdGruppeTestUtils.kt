package no.nav.modiapersonoversikt.utils

fun withTestGruppeIder(fn: TestUtils.UnsafeRunneable) {
    TestUtils.withEnv("MODIA_GENERELL_TILGANG_ID", "uuid-modia-generell") {
        TestUtils.withEnv("MODIA_OPPFOLGING_ID", "uuid-modia-oppfolging") {
            TestUtils.withEnv("SYFO_SENSITIV_ID", "uuid-syfo-sensitiv") {
                TestUtils.withEnv("STRENGT_FORTROLIG_ADRESSE_ID", "uuid-strengt-fortrolig") {
                    TestUtils.withEnv("FORTROLIG_ADRESSE_ID", "uuid-fortrolig") {
                        TestUtils.withEnv("EGNE_ANSATTE_ID", "uuid-egne-ansatte") {
                            fn.call()
                        }
                    }
                }
            }
        }
    }
}
