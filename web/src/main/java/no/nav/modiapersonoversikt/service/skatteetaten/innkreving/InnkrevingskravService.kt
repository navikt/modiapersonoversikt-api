package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.models.Kravlinje
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.joda.time.LocalDateTime
import kotlin.math.round

class InnkrevingskravService(
    private val innkrevingskravClient: InnkrevingskravClient,
    private val unleash: UnleashService,
) {
    fun hentInnkrevingskrav(innkrevingskravId: InnkrevingskravId): Krav? =
        if (unleash.isEnabled(Feature.SKATTEETATEN_INNKREVING_API_MOCK.propertyKey)) {
            Krav(
                kravType = "kravType",
                kravId = round(Math.random() * 10000).toString(),
                kid = round(Math.random() * 10000000).toString(),
                debitor =
                    Debitor(
                        debitorId = round(Math.random() * 100).toString(),
                        name = "Debitor 1",
                        identType = IdentType.FNR,
                        ident = "10108000398",
                    ),
                kreditor =
                    Kreditor(
                        kreditorId = round(Math.random() * 100).toString(),
                        name = "Debitor 1",
                        identType = IdentType.ORG_NR,
                        ident = "101080003",
                    ),
                posteringer =
                    listOf(
                        KravPostering(
                            kode = "KRL1",
                            beskrivelse = "Beskrivelse 3",
                            opprinneligBelop = 1000.0,
                            betaltBelop = 500.0,
                            gjenstaendeBelop = 500.0,
                            opprettetDato = LocalDateTime.parse("2024-05-14T12:00:00"),
                        ),
                        KravPostering(
                            kode = "KRL2",
                            beskrivelse = "Beskrivelse 3",
                            opprinneligBelop = 500.0,
                            betaltBelop = 300.0,
                            gjenstaendeBelop = 200.0,
                            opprettetDato = LocalDateTime.parse("2024-06-14T12:00:00"),
                        ),
                        KravPostering(
                            kode = "KRL3",
                            beskrivelse = "Beskrivelse 3",
                            opprinneligBelop = 200.0,
                            betaltBelop = 100.0,
                            gjenstaendeBelop = 100.0,
                            opprettetDato = LocalDateTime.parse("2024-07-14T12:00:00"),
                        ),
                    ),
                opprettetDato = LocalDateTime.parse("2024-05-14T12:00:00"),
            )
        } else {
            innkrevingskravClient
                .hentInnkrevingskrav(
                    innkrevingskravId,
                )?.toDomain()
        }

    fun hentAlleInnkrevingskrav(fnr: Fnr): List<Krav> =
        if (unleash.isEnabled(Feature.SKATTEETATEN_INNKREVING_API_MOCK.propertyKey)) {
            (1..(1..10).random()).map {
                Krav(
                    kravType = "kravType",
                    kravId = round(Math.random() * 10000).toString(),
                    kid = round(Math.random() * 10000000).toString(),
                    debitor =
                        Debitor(
                            debitorId = round(Math.random() * 100).toString(),
                            name = "Debitor 1",
                            identType = IdentType.FNR,
                            ident = "10108000398",
                        ),
                    kreditor =
                        Kreditor(
                            kreditorId = round(Math.random() * 100).toString(),
                            name = "Debitor 1",
                            identType = IdentType.ORG_NR,
                            ident = "101080003",
                        ),
                    posteringer =
                        listOf(
                            KravPostering(
                                kode = "KRL1",
                                beskrivelse = "Beskrivelse 3",
                                opprinneligBelop = 1000.0,
                                betaltBelop = 500.0,
                                gjenstaendeBelop = 500.0,
                                opprettetDato = LocalDateTime.parse("2024-05-14T12:00:00"),
                            ),
                            KravPostering(
                                kode = "KRL2",
                                beskrivelse = "Beskrivelse 3",
                                opprinneligBelop = 500.0,
                                betaltBelop = 300.0,
                                gjenstaendeBelop = 200.0,
                                opprettetDato = LocalDateTime.parse("2024-06-14T12:00:00"),
                            ),
                            KravPostering(
                                kode = "KRL3",
                                beskrivelse = "Beskrivelse 3",
                                opprinneligBelop = 200.0,
                                betaltBelop = 100.0,
                                gjenstaendeBelop = 100.0,
                                opprettetDato = LocalDateTime.parse("2024-07-14T12:00:00"),
                            ),
                        ),
                    opprettetDato = LocalDateTime.parse("2024-05-14T12:00:00"),
                )
            }
        } else {
            innkrevingskravClient
                .hentAlleInnkrevingskrav(fnr)
                .map { it.toDomain() }
        }
}

private fun Kravlinje.toDomain(): KravPostering =
    KravPostering(
        kode = "",
        beskrivelse = "",
        opprinneligBelop = opprinneligBeloep,
        betaltBelop = 0.0,
        gjenstaendeBelop = gjenstaaendeBeloep ?: 0.0,
        opprettetDato = LocalDateTime.now(),
    )

private fun Innkrevingskrav.toDomain(): Krav =
    Krav(
        posteringer = krav.map(Kravlinje::toDomain),
        kravId = "",
        kid = "",
        kravType = "",
        debitor = Debitor("", "", IdentType.FNR, ""),
        kreditor = Kreditor("", "", IdentType.ORG_NR, ""),
        opprettetDato = LocalDateTime.now(),
    )
