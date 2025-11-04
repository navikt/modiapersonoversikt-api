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
      "datoKravMottat": "2023-09-01",
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
      "datoKravMottat": "2023-06-01",
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

    @Test
    fun `skal returnere foreldrepenge`() {
        val mockForeldrepengerResponseBody =
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                """{
  "foreldrepenger": [
    {
      "forelder": "12345678910",
      "andreForeldersFnr": "10987654321",
      "antallBarn": 2,
      "barnetsFodselsdato": "2023-02-15",
      "dekningsgrad": 80.0,
      "fedrekvoteTom": "2023-09-01",
      "modrekvoteTom": "2023-12-01",
      "foreldrepengetype": "Utbetaling",
      "graderingsdager": 10,
      "restDager": 30,
      "rettighetFom": "2023-01-01",
      "eldsteIdDato": "2023-02-01",
      "foreldreAvSammeKjonn": "false",
      "periode": [
        {
          "fodselsnummer": "12345678910",
          "harAleneomsorgFar": true,
          "harAleneomsorgMor": false,
          "arbeidsprosentMor": 50.0,
          "avslagsaarsak": null,
          "avslaatt": "false",
          "disponibelGradering": 80.0,
          "erFedrekvote": true,
          "forskyvelsesaarsak1": "Sykdom",
          "forskyvelsesperiode1": {
            "fom": "2023-03-01",
            "tom": "2023-04-01"
          },
          "forskyvelsesaarsak2": "Ferie",
          "forskyvelsesperiode2": {
            "fom": "2023-05-01",
            "tom": "2023-06-01"
          },
          "foreldrepengerFom": "2023-02-01",
          "midlertidigStansDato": "2023-07-01",
          "erModrekvote": false,
          "morSituasjon": "Arbeid",
          "rettTilFedrekvote": "true",
          "rettTilModrekvote": "true",
          "stansaarsak": "Ukjent",
          "historiskeUtbetalinger": [
            {
              "periode": {
                "fom": "2023-04-01",
                "tom": "2023-04-30"
              },
              "belop": 15000.0
            }
          ],
          "kommendeUtbetalinger": [
            {
              "periode": {
                "fom": "2023-08-01",
                "tom": "2023-08-31"
              },
              "belop": 20000.0
            }
          ]
        }
      ],
      "slutt": "2023-12-31",
      "arbeidsforhold": [
        {
          "arbeidsgiverNavn": "Eksempel AS",
          "arbeidsgiverKontonr": "1234 56 78901",
          "inntektsperiode": "Månedlig",
          "inntektForPerioden": 50000.0,
          "sykepengerFom": "2023-01-01",
          "refusjonTom": "2023-06-01",
          "refusjonstype": "Full refusjon"
        }
      ],
      "erArbeidsgiverperiode": false,
      "arbeidskategori": "Ansatt",
      "omsorgsovertakelse": "Nei",
      "termin": "2023-03-01"
    }
  ]
}
""",
            )
        every { mockResponse.body } returns mockForeldrepengerResponseBody

        val result = api.hentForeldrepenger(fnr, start, slutt)
        assertEquals(fnr, result.foreldrepenger?.first()?.forelder)
        assertEquals(1, result.foreldrepenger?.size)
        verify { httpClient.newCall(any()) }
    }

    @Test
    fun `skal returnere pleiepenger`() {
        val mockPleiepengerResponseBody =
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                """{
  "pleiepenger": [
    {
      "barnet": "12345678910",
      "omsorgsperson": "98765432101",
      "andreOmsorgsperson": "56789012345",
      "restDagerFomIMorgen": 15,
      "forbrukteDagerTomIDag": 30,
      "pleiepengedager": 45,
      "restDagerAnvist": 10,
      "perioder": [
        {
          "fom": "2023-01-01",
          "antallPleiepengedager": 5,
          "arbeidsforhold": [
            {
              "arbeidsgiverNavn": "Eksempel AS",
              "arbeidsgiverKontonr": "1234 56 78901",
              "inntektsperiode": "Månedlig",
              "refusjonTom": "2023-03-01",
              "refusjonstype": "Full refusjon",
              "arbeidsgiverOrgnr": "555555555",
              "arbeidskategori": "Ansatt",
              "inntektForPerioden": 50000.00
            }
          ],
          "vedtak": [
            {
              "periode": {
                "fom": "2023-01-01",
                "tom": "2023-01-05"
              },
              "kompensasjonsgrad": 100,
              "utbetalingsgrad": 80,
              "anvistUtbetaling": "2023-01-10",
              "bruttobelop": 2500.50,
              "dagsats": 500.10,
              "pleiepengegrad": 80
            }
          ]
        }
      ]
    }
  ]
}
""",
            )
        every { mockResponse.body } returns mockPleiepengerResponseBody

        val result = api.hentPleiepenger(fnr, start, slutt)
        assertEquals(fnr, result.pleiepenger?.first()?.barnet)
        assertEquals(1, result.pleiepenger?.size)
        verify { httpClient.newCall(any()) }
    }
}
