package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders

import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.felleskodeverk.FellesKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.sfhenvendelse.SfHenvendelseKodeverk

data class KodeverkProviders(
    val fellesKodeverk: FellesKodeverk.Provider,
    val sfHenvendelseKodeverk: SfHenvendelseKodeverk.Provider,
    val oppgaveKodeverk: OppgaveKodeverk.Provider,
)
