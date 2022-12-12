package no.nav.modiapersonoversikt.infrastructure.naudit

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.AuditResource

class AuditResources {
    class Introspection {
        companion object {
            val Tokens = AuditResource("introspection.token")
            val Pdlsok = AuditResource("introspection.pdlsok")
        }
    }
    class Enhet {
        companion object {
            val Kontaktinformasjon = AuditResource("enhet.kontaktinformasjon")
            val Ansatte = AuditResource("enhet.ansatte")
            val OppgaveBehandlere = AuditResource("enhet.oppgavebehandlere")
            val Foreslatte = AuditResource("enhet.foreslatte")
        }
    }

    class Saksbehandler {
        companion object {
            @JvmField
            val NavnOgEnheter = AuditResource("saksbehandler.navnOgEnheter")

            @JvmField
            val Enheter = AuditResource("saksbehandler.enheter")
        }
    }

    class Person {
        class Henvendelse {
            companion object {
                @JvmField
                val Les = AuditResource("person.henvendelse.les")

                @JvmField
                val Sok = AuditResource("person.henvendelse.sok")
                val Opprettet = AuditResource("person.henvendelse.opprettet")
                val Ferdigstill = AuditResource("person.henvendelse.ferdigstill")
                val SlaSammen = AuditResource("person.henvendelse.slasammen")
                val Print = AuditResource("person.henvendelse.print")

                @JvmField
                val Journalfor = AuditResource("person.henvendelse.journalfor")
            }

            class Merk {
                companion object {
                    val Feilsendt = AuditResource("person.henvendelse.merk.feilsendt")
                    val Sladding = AuditResource("person.henvendelse.merk.sladding")
                    val SladdeArsaker = AuditResource("person.henvendelse.merk.sladding.arsaker")
                    val Lukk = AuditResource("person.henvendelse.merk.lukk")
                }
            }

            class Oppgave {
                companion object {
                    val Opprett = AuditResource("person.henvendelse.oppgave.opprett")
                    val Tildelte = AuditResource("person.henvendelse.oppgave.tildel")
                    val Metadata = AuditResource("person.henvendelse.oppgave.metadata")
                    val Avslutt = AuditResource("person.henvendelse.oppgave.avslutt")
                }
            }
        }

        companion object {
            val Kontaktinformasjon = AuditResource("person.kontaktinformasjon")
            val Oppfolging = AuditResource("person.oppfolging")

            @JvmField
            val Ytelser = AuditResource("person.ytelser")

            @JvmField
            val Kontrakter = AuditResource("person.kontrakter")
            val YtelserOgKontrakter = AuditResource("person.ytelserogkontrakter")

            @JvmField
            val Personalia = AuditResource("person.personalia")
            val Tilgang = AuditResource("person.tilgang")
            val Saker = AuditResource("person.saker")

            @JvmField
            val GsakSaker = AuditResource("person.gsaksaker")

            @JvmField
            val PesysSaker = AuditResource("person.pesyssaker")
            val Dokumenter = AuditResource("person.dokumenter")
            val Utbetalinger = AuditResource("person.utbetalinger")
            val Vergemal = AuditResource("person.vergemal")

            @JvmField
            val Sykepenger = AuditResource("person.sykepenger")

            @JvmField
            val Foreldrepenger = AuditResource("person.foreldrepenger")

            @JvmField
            val Pleiepenger = AuditResource("person.pleiepenger")

            @JvmField
            val Varsler = AuditResource("person.varsler")

            val EnhetTrace = AuditResource("trace.enhet-bruk")
        }
    }

    class Personsok {
        companion object {
            val Resultat = AuditResource("personsok.resultat")
            val Sokefelter = AuditResource("personsok.trace.sokefelter")
        }
    }
}
