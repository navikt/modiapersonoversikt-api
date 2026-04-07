package no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArenaInfotrygdApiTest {
    private val baseUrl = "http://example.com"
    private val httpClient = mockk<OkHttpClient>()
    private val mockCall = mockk<Call>()
    private val mockResponse = mockk<Response>()
    private val api = ArenaInfotrygdApiImpl(baseUrl, httpClient)
    private val fnr = "12345678910"
    private val start = "2023-01-01"
    private val slutt = "2023-12-31"

    @BeforeEach
    fun setup() {
        every { httpClient.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns mockResponse
        every { mockResponse.isSuccessful } returns true
    }

    @Test
    fun `skal returnere ytelseskontraktrt`() {
        val mockYtelseskontrakterResponseBody =
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                """{
  "ytelser": [
    {
      "type": "Dagpenger",
      "status": "Aktiv",
      "datoKravMottatt": "2023-09-01",
      "vedtak": [
        {
          "vedtakstype": "Innvilgelse",
          "activeFrom": "2023-09-05",
          "activeTo": "2023-12-31",
          "vedtaksdato": "2023-09-01",
          "vedtakstatus": "Gjeldende",
          "aktivitetsfase": "Arbeid"
        },
        {
          "vedtakstype": "Forlengelse",
          "activeFrom": "2023-10-01",
          "activeTo": "2023-12-31",
          "vedtaksdato": "2023-10-01",
          "vedtakstatus": "Gjeldende",
          "aktivitetsfase": "Arbeid"
        }
      ],
      "fom": "2023-09-01",
      "tom": "2023-12-31",
      "dagerIgjenMedBortfall": 90,
      "ukerIgjenMedBortfall": 13
    },
    {
      "type": "Arbeidsavklaringspenger",
      "status": "Avsluttet",
      "datoKravMottatt": "2023-06-01",
      "vedtak": [
        {
          "vedtakstype": "Avslag",
          "activeFrom": "2023-06-01",
          "activeTo": "2023-08-31",
          "vedtaksdato": "2023-08-01",
          "vedtakstatus": "Avsluttet",
          "aktivitetsfase": "Avklaring"
        }
      ],
      "fom": "2023-06-01",
      "tom": "2023-08-31",
      "dagerIgjenMedBortfall": 0,
      "ukerIgjenMedBortfall": 0
    }
  ],
  "rettighetsgruppe": "Dagpenger"
}
""",
            )

        every { mockResponse.body } returns mockYtelseskontrakterResponseBody

        val result = api.hentYtelseskontrakter(fnr, start, slutt)
        assertEquals(2, result.ytelser.size)
        verify { httpClient.newCall(any()) }
    }

    @Test
    fun `skal returnere oppfolgingskontrakter`() {
        val mockOppfolgingskontrakterResponseBody =
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                """{
  "syfoPunkter": [
    {
      "status": "Fullført",
      "dato": "2023-10-01",
      "syfoHendelse": "Veiledningsmøte",
      "fastOppfolgingspunkt": true
    },
    {
      "status": "Pågår",
      "dato": "2023-10-15",
      "syfoHendelse": "Arbeidstrening",
      "fastOppfolgingspunkt": false
    }
  ],
  "bruker": {
    "meldeplikt": true,
    "formidlingsgruppe": "ARBS",
    "innsatsgruppe": "Standardinnsats",
    "rettighetsgruppe": "Dagpenger",
    "sykmeldtFrom": "2023-09-01"
  },
  "vedtaksdato": "2023-10-01"
}

""",
            )
        every { mockResponse.body } returns mockOppfolgingskontrakterResponseBody

        val result = api.hentOppfolgingskontrakter(fnr, start, slutt)
        assertEquals(2, result.syfoPunkter.size)
        verify { httpClient.newCall(any()) }
    }

    @Test
    fun `skal returnere sykepenger`() {
        val mockSykepengerResponseBody =
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                """{
  "sykepenger": [
    {
      "fodselsnummer": "12345678910",
      "sykmeldtFom": "2023-01-01",
      "forbrukteDager": 120,
      "ferie1": {
        "start": "2023-07-01",
        "slutt": "2023-07-15"
      },
      "ferie2": {
        "start": "2023-08-01",
        "slutt": "2023-08-05"
      },
      "sanksjon": {
        "start": "2023-09-01",
        "slutt": "2023-09-10"
      },
      "stansaarsak": "Manglende dokumentasjon",
      "unntakAktivitet": "Ingen unntak",
      "forsikring": {
        "forsikringsordning": "NAV Ordning",
        "premiegrunnlag": 450000.50,
        "erGyldig": true,
        "forsikret": {
          "start": "2022-01-01",
          "slutt": "2023-12-31"
        }
      },
      "sykmeldinger": [
        {
          "sykmelder": "Dr. Hansen",
          "behandlet": "2023-01-10",
          "sykmeldt": {
            "start": "2023-01-01",
            "slutt": "2023-01-20"
          },
          "sykmeldingsgrad": 100.0,
          "gjelderYrkesskade": {
            "yrkesskadeart": "Fallskade",
            "skadet": "2023-01-05",
            "vedtatt": "2023-01-15"
          },
          "gradAvSykmeldingListe": [
            {
              "gradert": {
                "start": "2023-01-01",
                "slutt": "2023-01-10"
              },
              "sykmeldingsgrad": 50.0
            },
            {
              "gradert": {
                "start": "2023-01-11",
                "slutt": "2023-01-20"
              },
              "sykmeldingsgrad": 100.0
            }
          ]
        }
      ],
      "historiskeUtbetalinger": [],
      "kommendeUtbetalinger": [],
      "utbetalingerPaaVent": [
        {
          "vedtak": {
            "start": "2023-02-01",
            "slutt": "2023-02-15"
          },
          "utbetalingsgrad": 80.0,
          "oppgjorstype": "Ordinær",
          "arbeidskategori": "Ansatt",
          "stansaarsak": "Ferieavvik",
          "ferie1": {
            "start": "2023-02-01",
            "slutt": "2023-02-05"
          },
          "ferie2": null,
          "sanksjon": null,
          "sykmeldt": {
            "start": "2023-02-06",
            "slutt": "2023-02-10"
          }
        }
      ],
      "bruker": "Ola Nordmann",
      "midlertidigStanset": null,
      "slutt": "2023-12-31",
      "arbeidsforholdListe": [
        {
          "arbeidsgiverNavn": "Bedrift AS",
          "arbeidsgiverKontonr": "11112222333",
          "inntektsperiode": "Månedlig",
          "inntektForPerioden": 30000.0,
          "refusjonTom": "2023-06-30",
          "refusjonstype": "Delvis",
          "sykepengerFom": "2023-01-01"
        }
      ],
      "erArbeidsgiverperiode": false,
      "arbeidskategori": "Ansatt"
    }
  ]
}
""",
            )
        every { mockResponse.body } returns mockSykepengerResponseBody

        val result = api.hentSykepenger(fnr, start, slutt)
        assertEquals(fnr, result.sykepenger?.first()?.fodselsnummer)
        assertEquals(1, result.sykepenger?.size)
        verify { httpClient.newCall(any()) }
    }
}
