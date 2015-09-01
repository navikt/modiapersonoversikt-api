package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

@SuppressWarnings("checkstyle")
public class CmsSkrivestotteMock implements CmsSkrivestotte {

    private static int key = 0;
    private static final int RANDOM_TEKSTER = 20;

    @Override
    public List<SkrivestotteTekst> hentSkrivestotteTekster() {
        List<SkrivestotteTekst> tekster = new ArrayList<>();

        tekster.addAll(asList(
                skrivestotteTekst("Lang tekst",
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                                "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?",
                        "lang", "scroll"),
                skrivestotteTekst(
                        "Taushetsbelagt eller sensitiv informasjon",
                        "Takk for din henvendelse.\n" +
                                "\n" +
                                "Vi har dessverre ikke anledning til å besvare henvendelsen din per e-post, på grunn av personvern og taushetsplikt. \n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33, sende henvendelsen som ordinær post eller få informasjon ved personlig fremmøte ved ditt lokale NAV-kontor.\n" +
                                "\n" +
                                "Dersom du velger å sende inn henvendelsen per post, anbefaler vi at du henter ut en førsteside til saken din på www.nav.no. Alle dokumenter sendes til den adressen som er oppgitt på førstesiden.\n" +
                                "\n" +
                                "Søknadsskjemaer, selvbetjeningsløsninger, informasjon og «Dine utbetalinger» finner du på vår internettside www.nav.no. Her vil du også finne besøksadresse til ditt NAV-kontor.\n",
                        "generell", "sensitiv", "feilsendt"),
                skrivestotteTekst(
                        "Status i sak",
                        "Takk for din henvendelse til NAV. \n" +
                                "\n" +
                                "Din sak er fortsatt under behandling. Saksbehandlingstiden for denne type saker er normalt XX måneder. \n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33 dersom du har ytterligere spørsmål knyttet til din sak. Av hensyn til personvern og taushetsplikt kan vi ikke sende taushetsbelagt informasjon på e-post. \n",
                        "generell"),
                skrivestotteTekst(
                        "Krav om underskrift/skannet dokument",
                        "Takk for din henvendelse.\n" +
                                "\n" +
                                "E-posten du sendte inneholder opplysninger som må sendes inn med original underskrift. Dette av hensyn til personvern og informasjonssikkerhet.\n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33, sende henvendelsen som ordinær post eller få informasjon ved personlig fremmøte ved ditt lokale NAV-kontor.\n" +
                                "\n" +
                                "Dersom du velger å sende inn henvendelsen per post, anbefaler vi at du henter ut en førsteside til saken din på www.nav.no. Alle dokumenter sendes til den adressen som er oppgitt på førstesiden.\n" +
                                "\n" +
                                "Søknadsskjemaer, selvbetjeningsløsninger, informasjon og «Dine utbetalinger» finner du på vår internettside www.nav.no. Her vil du også finne besøksadresse til ditt NAV-kontor.\n",
                        "generell", "sensitiv", "feilsendt"),
                skrivestotteTekst("Bruker venter på at veileder tar kontakt", "[bruker.navn] venter på å bli kontaktet av veilederen sin. Fristen på to virkedager har gått ut. Jeg informerer om at vi kontakter lederen på NAV-kontoret for å sørge for at [bruker.fornavn] blir kontaktet så raskt som mulig.  ", "ks"),
                skrivestotteTekst("Spørsmål om saksbehandlingstid", "[bruker.navn] spør hvor langt søknaden om (YTELSE) har kommet. Søknaden er mottatt (DATO) og det er (ANTALL)uker saksbehandlingstid.\n" +
                        "\n" +
                        "[bruker.fornavn]  har fått informasjon om at hun/han kan følge med på Ditt NAV for status fremover.\n" +
                        "https://www.nav.no/no/NAV+og+samfunn/Om+NAV/Saksbehandlingstider+i+NAV\n" +
                        "", "ks"),
                skrivestotteTekst("Levert søknad", "[bruker.navn] har levert søknad om (YTELSE). NAV har mottatt søknaden og sender den videre til behandling. Jeg har opplyst om at behandlingstiden er på (ANTALL) uker og at det er mulig å følge med på Ditt NAV for status framover.", "søknad", "levering", "levert_søknad", "oppmøte"),
                skrivestotteTekst("Veiledning i hvordan søke", "[bruker.navn] har fått veiledning i hvordan søke om (YTELSE). Jeg har opplyst om at (TEKST).", "veiledning", "søknad"),
                skrivestotteTekst("Endre kontonummer - samtalereferat", "[bruker.navn] har fått veiledning i hvordan endre kontonummer. Jeg har opplyst om at kontonummer enkelt kan endres ved å logge inn på Ditt NAV eller ved å fylle ut skjema Melding om nytt bankkontonummer https://www.nav.no/no/Person/Skjemaer-for-privatpersoner/skjemaveileder/vedlegg?key=250914 og sende det til NAV.", "veiledning", "kontonummer"),
                skrivestotteTekst("Endre kontonummer", "Hei [bruker.fornavn]\n" +
                        "Du kan selv endre kontonummeret ditt ved å logge på Ditt NAV\n" +
                        "", "kontonummer"),
                skrivestotteTekst("Bruker.navn ", "[bruker.navn]", "KS"),
                skrivestotteTekst("Signatur internasjonalt ", "Med vennlig hilsen\n" +
                        "[saksbehandler.navn]\n" +
                        "NAV Kontaktsenter Internasjonalt ", "ks"),
                skrivestotteTekst("Generell informasjon om dagpenger ", "[bruker.navn] er arbeidsledig og skal registrere seg som arbeidssøker.\n" +
                        "\n" +
                        "Jeg har informert om at han/hun må:\n" +
                        "- registrere seg på nav.no\n" +
                        "- oppdatere / legge inn CV\n" +
                        "- sende meldekort\n" +
                        "- søke om dagpenger tidligst en uke før siste arbeidsdag\n" +
                        "\n" +
                        "[bruker.fornavn]kan følge status i saken sin på Ditt NAV, og se oppdatert saksbehandlingstid på nav.no.\n" +
                        "https://www.nav.no/no/NAV+og+samfunn/Om+NAV/Saksbehandlingstider+i+NAV", "ks"),
                skrivestotteTekst("Innbetaling uten KID", "Hei [bruker.fornavn]\n" +
                        "Merk innbetalingen med fødselsnummeret ditt (11 siffer).\n" +
                        "", "bidrag", "innkreving", "efaktura", "kidnummer"),
                skrivestotteTekst("Bidrag - klage på vedtak", "Hei [bruker.fornavn]\n" +
                        "Selv om du har klaget på vedtaket eller søkt om endring må du betale. Dette gir ikke automatisk betalingsutsettelse. Vedtaket gjelder fram til NAV gjør et nytt vedtak.\n" +
                        "", "bidrag", "innkreving"),
                skrivestotteTekst("Privat bidragsavtale", "Hei [bruker.fornavn]\n" +
                        "Privat oppgjør eller privat avtale betyr at bidragsmottageren og den bidragspliktige selv blir enige om størrelsen på bidraget og hvordan dette gjøres opp dere imellom. På nav.no kan dere lese mer om privat avtale, og bruke bidragskalkulatoren dersom dere ønsker hjelp til å fastsette beløpet.\n" +
                        "", "bidrag", "beregning", "privat_avtale"),
                skrivestotteTekst("Vedtak om arbeidsavklaringspenger etter flytting til utlandet", "[saksbehandler.enhet] har kommet frem til at [bruker.navn] vil ha rett til arbeidsavklaringspenger etter flyttingen sin til utlandet. Godkjenningen er tatt på bakgrunn av mottatte opplysninger og tilgjengelige saksdokumenter.\n" +
                        "\n" +
                        "Etter flyttingen skal ikke [bruker.fornavn] lenger ha kontakt med det nåværende NAV-kontoret, men vil i stedet få en fast saksbehandler hos [saksbehandler.enhet], som vil ha ansvaret for oppfølgingen så lenge [bruker.fornavn] mottar arbeidsavklaringspenger. Denne overføringen vil først gjennomføres når NAV har mottatt bekreftelse på flyttingen.\n" +
                        "\n" +
                        "Etter at [bruker.fornavn] har flyttet vil [saksbehandler.enhet] ta kontakt for å utarbeide en aktivitetsplan. Aktivitetsplanen skal beskrive hva som skal til for at [bruker.fornavn] skal komme i arbeid.\n" +
                        "\n" +
                        "Det er viktig at [bruker.fornavn] gjør følgende når flyttingen er gjennomført:\n" +
                        "- Melde flyttingen til myndighetene i bostedslandet\n" +
                        "- Gi NAV skriftlig beskjed om ny bostedsadresse (dette kan også oppdateres på Ditt NAV)\n" +
                        "- {FYLL INN ANDRE AKTIVITETER}\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "AAP", "Utland", "Eksport"),
                skrivestotteTekst("Medlemskap - ikke registrert unntak fra norsk trygd", "[bruker.navn] har fått informasjon om at NAV ikke har registrert unntak fra norsk trygd i perioden fra DATO til DATO. Normalt blir unntak fra norsk trygd registrert i løpet av to uker etter at vi har mottatt unntaket. Når perioden er registrert overføres dette elektronisk til Skatteetaten. [bruker.fornavn] blir bedt om å kontakte Skatteetaten for å få avgiften refundert.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Medlemskap", "Utland", "Unntak"),
                skrivestotteTekst("Reise til utlandet - Beholde stønaden", "[bruker.navn] har (YTELSE) og skal reise til utlandet. [bruker.fornavn] er informert om at han/hun må søke om å få beholde stønaden under oppholdet ved å sende inn søknadsskjema. Skjema og mer informasjon finnes på nav.no.\n" +
                        "", "ks"),
                skrivestotteTekst("Krykker", "Hei [bruker.fornavn]\n" +
                        "Du får krykker hvis du har et varig behov for det. Hvis du trenger krykker fordi du har brukket benet eller lignende, må du skaffe deg krykker selv. Du kan også kontakte kommunen du bor i.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "hms"),
                skrivestotteTekst("Bilforsikring", "Hei [bruker.fornavn]\n" +
                        "Hvis du har fått tilskudd til bil velger du selv hvilken type forsikring du vil ha på bilen. Har du fått lån til en kassebil med heis eller rampe, må denne være fullkaskoforsikret med salgspant og gjeldsbrev til NAV. Det må den være fra den dagen du får bilen utlevert fra bilforhandler og til den dagen bilen blir levert tilbake. Den som bygger om bilen er ansvarlig for at bilen er forsikret mens den er under ombygging.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "forsikring", "hms"),
                skrivestotteTekst("Bistand til å søke", "[bruker.navn] har fått bistand til å søke om (YTELSE). Jeg har opplyst om at (TEKST).", "veiledning", "bistand", "søknad"),
                skrivestotteTekst("Tidspunkt for utbetaling av bidrag", "Hei [bruker.fornavn]\n" +
                        "Du vil få utbetalt bidraget når innbetalingen fra den bidragspliktige er klargjort hos NAV Innkreving. Dette tar som regel tre virkedager.\n" +
                        "Hvis vi ikke har kontonummeret til bidragsmottakeren, vil utbetalingen ta lengre tid.\n" +
                        "Bidragspliktig må betale den 25. hver måned.\n" +
                        "", "bidrag", "innkreving", "utbetaling"),
                skrivestotteTekst("Medlemsskap - Opphold i utlandet", "[bruker.navn] har fått generell informasjon om medlemskap i folketrygden ved opphold i utlandet. Videre er [bruker.fornavn] informert om at så lenge utenlandsoppholdet varer kortere enn 12 måneder vil HAN/HUN fortsatt være medlem i folketrygden.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Medlemskap", "Utland", "12mnd"),
                skrivestotteTekst("Etterlyser sykepenger - opplyst om utbetalingsdato", "[bruker.navn] etterlyser sykepenger for perioden (FRADATO-TILDATO). Jeg har sjekket at nødvendig dokumentasjon foreligger og opplyst om at sykepengene blir utbetalt innen siste virkedag i måneden.", "sykepenger", "utbetaling"),
                skrivestotteTekst("Støtte til reise ved reparasjon av bil", "Hei [bruker.fornavn]\n" +
                        "NAV har ingen biler til utlån mens bilen din er til reparasjon. Hvis du bruker bil til og fra arbeid, kan du søke om å få støtte til arbeids- og utdanningsreiser den perioden du er uten bil. Du finner informasjon om dette på https://www.nav.no/no/Person/Skjemaer-for-privatpersoner/Skjemaer/Arbeid%2C+helse+og+sykdom/Arbeids-+og+utdanningsreiser\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "reparasjon"),
                skrivestotteTekst("Den bidragspliktige deler bolig", "Hei [bruker.fornavn]\n" +
                        "Dersom den bidragspliktige deler bolig med annen voksen vil det bli tatt hensyn til i beregningen. Hva det har å si konkret når det gjelder størrelsen på bidraget må beregnes i hver enkelt sak. Du kan gjøre dette selv ved å bruke bidragskalkulatoren på nav.no\n" +
                        "", "bidrag"),
                skrivestotteTekst("Etterspør utbetaling ", "[bruker.navn] etterspør utbetaling av (YTELSE). Utbetalingen vil være på konto i løpet av én til tre dager avhengig av bankforbindelse.\n" +
                        "", "ks"),
                skrivestotteTekst("Generell bistand", "[bruker.navn] har fått bistand til å (TEKST). Jeg har opplyst om at (TEKST).", "veiledning", "bistand"),
                skrivestotteTekst("Fjerne betalingsanmerkning –  innkreving av bidrag", "Hei [bruker.fornavn]\n" +
                        "For å bli kvitt en betalingsanmerkning må du betale gjelden din til NAV. Hvis du betaler bidrag må du i tillegg lage avtale med NAV Innkreving for betaling av framtidige bidrag.\n" +
                        "", "bidrag", "innkreving", "gjeld"),
                skrivestotteTekst("Forfallsdato innkreving av bidrag", "Hei [bruker.fornavn]\n" +
                        "Bidrag har forfallsdato den 25. hver måned. Forfallsdatoen kan ikke endres.\n" +
                        "", "bidrag", "innkreving", "forfallsdato"),
                skrivestotteTekst("Oppsettende virkning/stans i innkreving", "Hei [bruker.fornavn]\n" +
                        "Du kan søke om oppsettende virkning til NAV-enheten som gjorde vedtaket om bidrag. Dersom NAV Innkreving benytter tvangstiltak, vil du også kunne kreve at vi tar stilling til innsigelsen din.\n" +
                        "", "bidrag", "innkreving"),
                skrivestotteTekst("Flytte til utlandet - AAP - Innenfor EØS", "[bruker.navn] ønsker å flytte til utlandet, og vil vite om det er mulig å beholde arbeidsavklaringspengene etter flyttingen.\n" +
                        "\n" +
                        "[bruker.fornavn] kan bare ta med seg arbeidsavklaringspengene innenfor EØS-området. HAN/HUN kan ikke ta med seg arbeidsavklaringspenger ved flytting utenfor EØS-området.\n" +
                        "\n" +
                        "Hvis [bruker.fornavn] skal flytte til et EØS-land, må [saksbehandler.enhet] vurdere om [bruker.fornavn] kan ta med seg arbeidsavklaringspengene til det aktuelle landet.\n" +
                        "\n" +
                        "For at [saksbehandler.enhet] skal kunne vurdere videre rett til arbeidsavklaringspenger, må [bruker.fornavn] fylle ut egenvurderingsskjemaet på nytt, dette finnes under skjemaveilederen på nav.no. I tillegg vil [bruker.fornavn] få tilsendt et eget spørreskjema som må besvares og returneres til [saksbehandler.enhet].\n" +
                        "\n" +
                        "[saksbehandler.enhet] vil vurdere saken når alle nødvendige opplysninger foreligger. [bruker.navn] må oppholde seg i Norge og fortsette med de avtalte aktivitetene inntil det kommer et endelig svar i posten.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "AAP", "Utland", "Eksport"),
                skrivestotteTekst("Bruker.fornavn ", "[bruker.fornavn]", "ks"),
                skrivestotteTekst("Medlemskap ved arbeid innenfor EØS", "[bruker.navn] har fått generell informasjon om medlemskap i folketrygden ved arbeid innenfor EØS-området. [bruker.fornavn] er veiledet til søknadsskjemaet på nav.no.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Medlemskap", "Utland", "EØS"),
                skrivestotteTekst("Medlemsskap - Støtte fra Lånekassen", "[bruker.navn] har fått generell informasjon om medlemskap i folketrygden ved studier i utlandet. Videre er [bruker.fornavn] informert om at ved støtte fra Lånekassen til utenlandsstudiene vil HAN/HUN fortsatt være medlem i folketrygden under studiene.\n" +
                        "\n" +
                        "Dersom [bruker.fornavn] får avslag fra Lånekassen må HUN/HAN søke om medlemskap i folketrygden. [bruker.fornavn] er veiledet til søknadsskjemaet på nav.no.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Medlemskap", "Utland", "Student"),
                skrivestotteTekst("For reparasjon av hjelpemiddel kontakt kommunen", "Hei [bruker.fornavn]\n" +
                        "Du kontakter kommunen for å få hjelp til å reparere hjelpemiddelet ditt.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "reparasjon", "hms"),
                skrivestotteTekst("Årsavgift og bil", "Hei [bruker.fornavn]\n" +
                        "NAV betaler årsavgiften når du får stønad til kassebil med heis eller rampe. Det året bilen blir innlevert er det den som var eier ved årsskiftet som må betale årsavgiften.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "hms"),
                skrivestotteTekst("Søke innkreving av bidrag", "Hei [bruker.fornavn]\n" +
                        "Vi ber om at du sender oss et brev eller tar kontakt med oss på telefon 55 55 33 33 for å informere om at du også søker innkreving av bidrag.\n" +
                        "", "bidrag", "innkreving"),
                skrivestotteTekst("Endring i samvær", "Hei [bruker.fornavn]\n" +
                        "Er det endret samvær eller endringer i inntekt må du søke om endring på eget skjema. Skjemaet finner du på www.nav.no.\n" +
                        "", "bidrag", "samvær"),
                skrivestotteTekst("Innkreving av bidrag – trekk i lønn", "Hei [bruker.fornavn]\n" +
                        "Hvis den bidragspliktige ikke innbetaler på giroen som NAV Innkreving sender, vil vi tidligst sette i gang lønnstrekk etter fire måneder.\n" +
                        "", "bidrag", "trekk"),
                skrivestotteTekst("Ønsker kontakt med veileder", "[bruker.navn] ønsker å bli kontaktet av veilederen sin. Jeg har gitt beskjed til NAV-kontoret og informert om at han/hun kan forvente å bli kontaktet innen to virkedager.", "ks"),
                skrivestotteTekst("Generell veiledning", "[bruker.navn] har fått generell veiledning om (YTELSE). Jeg har opplyst om at (TEKST).", "veiledning", "generell", "veiledning"),
                skrivestotteTekst("Registrere seg på nav.no/meldekort", "[bruker.navn] har fått bistand til å registrere seg på nav.no og generell veiledning om meldekort. ", "veiledning", "bistand", "registrering", "arbeidssøker"),
                skrivestotteTekst("Informasjon om vilkår for bil og søknad", "Hei [bruker.fornavn]\n" +
                        "Du finner informasjon om vilkårene for å få støtte til bil, og søknadsskjema på https://www.nav.no/no/Person/Hjelpemidler/Tjenester+og+produkter/Bil+og+transport.\n" +
                        "\n" +
                        "Hvis du har spørsmål kan du kontakte oss på telefon 55 55 33 35.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "hms"),
                skrivestotteTekst("Begrunnelse for betalingsanmerkning", "Hei [bruker.fornavn]\n" +
                        "Det er fordi NAV Innkreving har et tvangstiltak mot deg. Det kan for eksempel være trekk i lønn eller ytelse. NAV Innkreving kan bare ta utleggstrekk i lønn eller andre ytelser i saker der du ikke betaler frivillig. Vi kan også ta utleggspant i formuesgoder (bankkonto, eiendom, bil, motorsykkel og så videre). Når vi tar utleggstrekk eller utleggspant vil du få en  betalingsanmerkning.\n" +
                        "", "bidrag", "innkreving", "trekk"),
                skrivestotteTekst("Signatur", "Med vennlig hilsen\n" +
                        "[saksbehandler.navn]\n" +
                        "NAV Kontaktsenter ", "ks"),
                skrivestotteTekst("Betalingsfrist barnebidrag", "Hei [bruker.fornavn]\n" +
                        "Du har plikt å betale innen fristen, selv om du ikke har mottatt giro en måned. Du kan bruke KID fra tidligere giro eller ta kontakt med NAV Innkreving på telefonnummer 21 05 11 00.\n" +
                        "", "bidrag", "innkreving", "giro"),
                skrivestotteTekst("Motregning i skatt", "Hei [bruker.fornavn]\n" +
                        "Får du penger til gode på skatten, kan NAV Innkreving kreve å få disse pengene til å dekke gjeld du har til NAV. Dette kalles skattemotregning.\n" +
                        "NAV Innkreving sender varsel om skattemotregning i alle saker hvor det er gjeld. Dette skjer selv om du har inngått avtale om avdragsordning eller betalingsutsettelse, eller om NAV har besluttet trekk i saken.\n" +
                        "Du kan klage når skatteoppkreveren har gjort motregningen. Klagefristen er én måned fra du mottar brevet fra skatteoppkreveren med melding om at du har blitt motregnet. Du kan ikke klage på forhåndsvarselet.\n" +
                        "", "bidrag", "innkreving", "gjeld", "skatt"),
                skrivestotteTekst("Venter barn - Søke om foreldrepenger", "[bruker.navn] venter barn og skal søke om foreldrepenger.\n" +
                        "\n" +
                        "Jeg har informert om at han/hun må:\n" +
                        "- Søke til rett tid (vist til nav.no): https://www.nav.no/no/Person/Familie/Venter+du+barn/Foreldrepenger/Husk+%C3%A5+s%C3%B8ke+til+rett+tid.403386.cms\n" +
                        "\n" +
                        "-Sende søknad om foreldrepenger, engangsstønad eller fedrekvote og mødrekvote ved fødsel og adopsjon: https://www.nav.no/no/Person/Skjemaer-for-privatpersoner/Skjemaer/Familie/Svangerskap+fodsel+adopsjon\n" +
                        "\n" +
                        "- Be arbeidsgiver fylle ut skjema for inntektsopplysninger – 08-30.01\n" +
                        "- Legge ved terminbekreftelse datert tidligst i svangerskapsuke 27 (kun mor)\n" +
                        "-Legge ved dokumentasjon på mors arbeid, studier eller sykdom (kun når far tar ut foreldrepenger som ikke er fedrekvote)\n" +
                        "\n" +
                        "[bruker.fornavn] kan følge statusen i saken sin på Ditt NAV og se oppdatert saksbehandlingstid på nav.no: https://www.nav.no/no/NAV+og+samfunn/Om+NAV/Saksbehandlingstider+i+NAV\n" +
                        "", "ks"),
                skrivestotteTekst("Purrer på sak", "[bruker.navn] purrer på saken. [bruker.fornavn] blir informert om at saken vil bli prioritert.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Medlemskap", "Utland", "Purring"),
                skrivestotteTekst("Venter barn - informasjon", "[bruker.navn] venter barn og har spørsmål om foreldrepenger.\n" +
                        "\n" +
                        "Jeg har:\n" +
                        "- informert om og henvist til nav.no for vilkår for foreldrepenger\n" +
                        "- informert om når mor/far skal søke (vist til nav.no): https://www.nav.no/no/Person/Familie/Venter+du+barn/Foreldrepenger/Husk+%C3%A5+s%C3%B8ke+til+rett+tid.403386.cms\n" +
                        "\n" +
                        "[bruker.fornavn] kan følge statusen i saken sin på Ditt NAV, og se oppdatert saksbehandlingstid på nav.no: https://www.nav.no/no/NAV+og+samfunn/Om+NAV/Saksbehandlingstider+i+NAV\n" +
                        "", "ks"),
                skrivestotteTekst("Familievernkontor", "Vi kan dessverre ikke hjelpe deg med å gjenopprette kontakt med barna, eller gi deg veiledning med hensyn til konfliktsituasjonen mellom deg og barnas mor eller far. Vi vil råde deg til å ta kontakt med familievernkontoret der du bor. Du kan finne nærmeste kontor på www.bufetat.no/familievernkontor.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Bidrag", "Utland", "Bufetat"),
                skrivestotteTekst("Medlemskap - unntak fra norsk trygd", "[bruker.navn] har fått informasjon om at NAV har registrert unntak fra norsk trygd i perioden fra DATO til DATO. Perioder med unntak blir elektronisk overført til Skatteetaten hver 14. dag. [bruker.fornavn] blir bedt om å kontakte Skatteetaten for å få avgiften refundert.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Medlemskap", "Utland", "Unntak"),
                skrivestotteTekst("Etterlyser sykepenger - midlertidig stanset", "[bruker.navn] etterlyser sykepenger for perioden (FRADATO-TILDATO). Jeg har opplyst om at sykepengene er midlertidig stanset på grunn av manglende aktivitet, og at hovedregelen for sykepenger ut over 8 uker, er at man deltar i arbeidsrelatert aktivitet. For de som har et arbeid, vil arbeidsrelatert aktivitet være å delta i eget ellet annet arbeid på arbeidsplassen,  helt eller delvis. Aktivitetskravet gjelder for alle sykmeldte, både de med og de uten arbeidsgiver. Hvis du ikke har en arbeidsgiver, er det NAV-kontoret ditt som skal følge deg opp.", "sykepenger", "stans", "aktivitet"),
                skrivestotteTekst("Søknadsskjema på nav.no - Hjelpemidler", "Hei [bruker.fornavn]\n" +
                        "Du finner søknadsskjema på www.nav.no. Du bør ta kontakt med kommunen du bor i for å få hjelp med å søke. Hjelpemiddelsentralen kan også hjelpe deg med å fylle ut søknadsskjemaet.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "søknad", "hms"),
                skrivestotteTekst("Kontakt NAV om bil ", "Hei [bruker.fornavn]\n" +
                        "Du finner informasjon om dette på https://www.nav.no/no/Person/Hjelpemidler/Tjenester+og+produkter/Bil+og+transport/Kontakt+NAV\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "spesialutstyr", "hms"),
                skrivestotteTekst("Virkningstidspunkt for fastsettelse av bidrag", "Hei [bruker.fornavn]\n" +
                        "Hovedregelen er at bidraget blir fastsatt fra den måneden du oppfyller vilkårene og NAV mottok søknaden, maksimalt tre år tilbake i tid.  Er det snakk om endring av bidraget er hovedregelen at bidraget endres fra måneden etter at du sendte søknad om endring til NAV. For mer informasjon om virkningstidspunktet som er fastsatt i saken din, må du ta kontakt med NAV på telefon 55 55 33 33.\n" +
                        "", "bidrag"),
                skrivestotteTekst("Bruker ønsker å klage - Ditt NAV", "[bruker.navn] ønsker å klage på vedtak om (YTELSE).\n" +
                        "[bruker.fornavn] er informert om at han/hun må klage skriftlig og at klagen må signeres. [bruker.fornavn] vil sende inn skriftlig klage via Ditt NAV / Skriv til oss som bekrefter klagen.\n" +
                        "", "ks"),
                skrivestotteTekst("Saksbehandlingstid oppsettende virkning ", "Hei [bruker.fornavn]\n" +
                        "Vanlig saksbehandlingstid er fire til seks uker.\n" +
                        "", "bidrag", "innkreving", "behandlingstid"),
                skrivestotteTekst("Reiseutgifter – samvær ", "Hei [bruker.fornavn]\n" +
                        "Deling av reiseutgifter i forbindelse med samvær mellom samværsforelderen og barnet er et forhold mellom foreldrene, og er ikke med i bidragsberegningen. Dersom dere ikke blir enige om hvordan dere skal dele reiseutgiftene, kan dere ta saken videre til fylkesmannen og/eller retten.\n" +
                        "", "bidrag"),
                skrivestotteTekst("Inngå samværsavtale - familievernkontor", "Hei [bruker.fornavn]\n" +
                        "NAV anbefaler at du tar kontakt med familievernkontoret i kommunen for å avtale meglingstime. Dersom dette ikke fører til noen endring, er det mulig å ta saken videre til domstolen for å få en avgjørelse der. Dere må søke NAV hvis det blir en endring i samværsavtalen som får betydning for størrelsen på bidraget.\n" +
                        "", "bidrag", "samvær"),
                skrivestotteTekst("Bruker ønsker å klage på vedtak - skjema", "[bruker.navn] ønsker å klage på vedtak om (YTELSE) og er informert om muligheten til å klage skriftlig via nav.no på skjema Klage på vedtak: https://www.nav.no/no/Person/Skjemaer-for-privatpersoner/Klage+p%C3%A5+vedtak ", "ks"),
                skrivestotteTekst("NAV har ringt - Bruker fikk ikke tatt telefonen", "[bruker.navn] har blitt kontaktet av NAV i dag, men fikk ikke tatt telefonen. [bruker.fornavn] avventer at veilederen ringer tilbake. Veilederen er ikke tilgjengelig for samtale nå, men får beskjed om at [bruker.fornavn] har forsøkt å ringe.  ", "ks"),
                skrivestotteTekst("Status i sak", "[bruker.navn] lurer på status i sak om (YTELSE). Jeg har opplyst at (STATUS) og at behandlingstiden er på (ANTALL) uker. Jeg informerte også om at det er mulig å følge med på Ditt NAV for status framover.", "status", "saksbehandling"),
                skrivestotteTekst("Etterlyser sykepenger for periode-mangler dokumentasjon", "[bruker.navn] etterlyser sykepenger for perioden (FRADATO-TILDATO). Jeg har informert om at NAV mangler (DOKUMENTASJON) som er nødvendig for at sykepengene skal bli utbetalt. Jeg har bedt om at (DOKUMENTASJON) leveres så fort som mulig.", "sykepenger", "dokumentasjon", "etterlyser"),
                skrivestotteTekst("Reparasjon av hjelpemiddel", "Hei [bruker.fornavn]\n" +
                        "Ja, i enkelte tilfeller. I utgangspunktet er det kommunen som er ansvarlig for å hjelpe deg mens hjelpemiddelet er til reparasjon. \n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "reparasjon", "hms"),
                skrivestotteTekst("Klage på vedtak om bidrag", "Hei [bruker.fornavn]\n" +
                        "Er du uenig i vedtaket må du klage. Klagefristen går fram av vedtaket ditt. Vi anbefaler at du sender klagen skriftlig, enten som et vanlig brev eller benytt klageskjema på www.nav.no.\n" +
                        "", "bidrag", "klage"),
                skrivestotteTekst("Registrere seg som arbeidssøker", "[bruker.navn] har fått bistand til å registrere seg som arbeidssøker. Jeg har opplyst om at det er viktig å gå gjennom disse punktene: \n" +
                        "\n" +
                        "- Les Arbeidsledig - hva nå? https://www.nav.no/no/Person/Arbeid/Arbeidsledig+og+jobbsoker/Arbeidsledig+-+hva+na\n" +
                        "\n" +
                        "- Det er viktig å registrere en god CV på nav.no, og husk å legge inn gode kompetanseord. \n" +
                        "- Les: Slik lager du en god CV https://www.nav.no/no/Person/Arbeid/Arbeidsledig+og+jobbsoker/Jobbsokertips/Slik+lager+du+en+god+CV\n" +
                        "\n" +
                        "- Gå gjennom Jobbsøkertips https://www.nav.no/no/Person/Arbeid/Arbeidsledig+og+jobbsoker/Jobbsokertips/Slik+lager+du+en+god+CV\n" +
                        "\n" +
                        "For mer informasjon, se www.nav.no eller kontakt NAV på telefon 55 55 33 33\n" +
                        "\n" +
                        "", "veiledning", "jobbsøkertips"),
                skrivestotteTekst("Bygge om bil", "Hei [bruker.fornavn] \n" +
                        "Du vil få beskjed av NAV om hvem som skal bygge om bilen din når dette er avklart.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "hms"),
                skrivestotteTekst("Søke om endring av bidragstrekk", "Hei [bruker.fornavn]\n" +
                        "Fyll ut Opplysningsblankett om økonomi for innkreving på https://www.nav.no/no/Person/Skjemaer-for-privatpersoner/skjemaveileder/vedlegg?key=248707&veiledertype=privatperson. Opplysningsskjemaet sender du til NAV Innkreving med dokumentasjon på husstandens samlede inntekter og utgifter. En veiledning ligger i opplysningsskjemaet. \n" +
                        "", "bidrag", "innkreving"),
                skrivestotteTekst("Saksbehandlingstid bidragssak", "Hei [bruker.fornavn]\n" +
                        "Behandlingstiden på bidragssaker er på [ANTALL] måneder.\n" +
                        "", "bidrag", "behandlingstid"),
                skrivestotteTekst("Barnets underholdskostnad", "Hei [bruker.fornavn]\n" +
                        "Bidraget blir beregnet med utgangspunkt i hva det koster å forsørge barnet (underholdskostnad). Underholdskostnaden består av forbruksutgifter, boutgifter og eventuelle tilsynsutgifter. Utgiftene varierer med alderen til barnet. Varene som danner grunnlag for beregningene, holder enkel god kvalitet og lav pris. Det er dessuten et langtidsbudsjett slik at det er lagt inn noe ekstra hver måned for å dekke mer sjeldne og større utgifter.\n" +
                        "", "bidrag", "beregning"),
                skrivestotteTekst("Flytte til utlandet - AAP - Utenfor EØS", "[bruker.navn] ønsker å flytte til utlandet, og vil vite om det er mulig å beholde arbeidsavklaringspengene etter flyttingen.\n" +
                        "\n" +
                        "[bruker.fornavn] kan bare ta med seg arbeidsavklaringspengene innenfor EØS-området. Det aktuelle landet er ikke innenfor EØS-området, og [bruker.fornavn] har derfor fått beskjed om at HAN/HUN ikke kan ta med seg arbeidsavklaringspengene hvis flyttingen gjennomføres.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "AAP", "Utland", "Eksport"),
                skrivestotteTekst("Levert dokumentasjon", "[bruker.navn] har levert (DOKUMENTASJON). Jeg har opplyst om at (TEKST).", "dokumentasjon", "levering", "levert_dokument", "oppmøte"),
                skrivestotteTekst("Levert manuelt korrigert meldekort", "[bruker.navn] har levert manuelt korrigert meldekort. NAV har mottatt meldekortet og sender det videre til behandling. Jeg har opplyst om at behandlingstiden er på (ANTALL) dager", "meldekort", "manuelt_meldekort", "oppmøte"),
                skrivestotteTekst("For vurdering av behov kontakt kommunen", "Hei [bruker.fornavn]\n" +
                        "Du bør kontakte kommunen for å få hjelpe til å vurdere behovet ditt for hjelpemidler. Hvis du og kommunens ansatte har behov for hjelp fra hjelpemiddelsentralen for å finne frem til riktig hjelpemiddel, finner dere henvisningsskjema på www.nav.no.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "vurdere_behov", "hms"),
                skrivestotteTekst("Dødsbo og bil", "Hei [bruker.fornavn] \n" +
                        "Dødsboet må innbetale det beløpet som ikke er nedskrevet til NAV. Hvis NAV har gitt lån til bilen, skal bilen innleveres til oss. Du kan ta direkte kontakt med det bilsenteret brukeren tilhørte for å få gjeldsoppgjør på bilen. Du finner kontaktinfo på https://www.nav.no/no/Person/Hjelpemidler/Tjenester+og+produkter/Hjelpemidler/Kontakt+hjelpemiddelsentralen \n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil"),
                skrivestotteTekst("Signatur", "Med vennlig hilsen\n" +
                        "[saksbehandler.navn]\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "signatur"),
                skrivestotteTekst("Ved reise til utlandet", "Hei [bruker.fornavn]\n" +
                        "Skal du til utlandet, må du alltid informere hjelpemiddelsentralen om dette før du reiser. Det er mulig å få reparert hjelpemidler i utlandet etter avtale med oss. Vi tar ofte service på utstyr før avreise, og/eller sender med slitedeler.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "reparasjon", "utlandet", "hms"),
                skrivestotteTekst("Stønad til tilskudd til bil", "Hei [bruker.fornavn] \n" +
                        "Du kan få ny stønad til tilskudd til bil til arbeid og utdanning hvis bilen er brukt i minst åtte år. Du kan få ny stønad til kassebil med heis eller rampe til arbeid og utdanning når den gamle bilen er minst åtte år og har gått 150 000 kilometer. Hvis du har fått stønaden til bruk i dagliglivet, må bilen være minst elleve år. Du kan søke om ny stønad tre måneder før brukstiden har gått ut.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "hms"),
                skrivestotteTekst("Utlevering av bil", "Hei [bruker.fornavn] \n" +
                        "Du kan hente bilen når alle gjeldsdokumentene i saken er underskrevet og returnert til bilsenteret. Pant i bilen må også være registrert i Brønnøysundregisteret. Hvis du er umyndig, må Fylkesmannen ha godkjent gjeldsdokumentene før du får utlevert bilen.\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "hms"),
                skrivestotteTekst("Gebyr for fastsettelse av bidrag", "Hei [bruker.fornavn]\n" +
                        "Nei, men dere må betale et gebyr hvis NAV skal fastsette bidraget. Dersom dere har avtalt bidrag privat, koster det ikke noe å ha innkreving gjennom NAV. \n" +
                        "", "bidrag", "innkreving", "fastsettelsesgebyr"),
                skrivestotteTekst("Betaling av bidrag med eFaktura og Avtalegiro", "Hei [bruker.fornavn]\n" +
                        "Du kan bruke eFaktura til å betale et hvilket som helst krav til NAV innkreving. Du kan bruke Avtalegiro i barnebidragssaker. Les mer om hvordan du bruker eFaktura og avtalegiro på giroen eller på https://www.nav.no/no/Person/Flere+tema/Innkreving+og+innbetaling/eFaktura+og+avtalegiro.406407.cms \n" +
                        "", "bidrag", "innkreving", "giro", "efaktura"),
                skrivestotteTekst("Bidrag - klage på vedtak", "Hei [bruker.fornavn]\n" +
                        "Selv om du har klaget på vedtaket eller søkt om endring må du betale. Dette gir ikke automatisk betalingsutsettelse. Vedtaket gjelder fram til NAV gjør et nytt vedtak.\n" +
                        "", "bidrag", "innkreving"),
                skrivestotteTekst("Klage på vedtak og oppsettende virkning", "Hei [bruker.fornavn]\n" +
                        "Hovedregelen er at bidraget løper til tross for at det er sendt inn en klage. Det er mulig å søke om å fryse bidraget, det heter oppsettende virkning.\n" +
                        "", "bidrag", "klage"),
                skrivestotteTekst("Bruker tar kontakt på nytt", "[bruker.navn] tar kontakt på nytt for å snakke med veilederen sin. Jeg opplyser om at veilederen har fått beskjed om å ta kontakt og at fristen går ut (DATO)", "ks"),
                skrivestotteTekst("Økonomisk rådgivning", "Vi vil også gjøre deg oppmerksom på NAVs økonomirådstelefon 800 45353 som kan gi økonomisk rådgivning, og hjelp til selvhjelp om det å komme i gang med å finne løsninger på betalingsproblemer. \n" +
                        "\n" +
                        "Nummeret er gratis å ringe til fra fasttelefon. Hvis du ringer fra mobil vil du få tilbud om å bli ringt opp med én gang du kommer igjennom til tjenesten. Åpningstidene til 800gjeld er kl. 09.00-15.00.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Bidrag", "Utland", "Gjeld"),
                skrivestotteTekst("Underholdsbidrag og bidragsforskudd", "Hvis barnet er plassert utenfor hjemmet, får du ikke underholdsbidrag og bidragsforskudd etter hovedregelen måneden etter plasseringen. \n" +
                        "\n" +
                        "Den forelderen barnet ikke bor fast hos betaler underholdsbidrag.\n" +
                        "\n" +
                        "NAV utbetaler bidragsforskudd hver måned hvis det ikke er betalt bidrag, eller hvis bidraget er fastsatt til et lavere beløp enn det som kan gis i forskudd. Forskuddsmottakeren har plikt til å gi alle opplysninger som kan ha betydning for retten til bidragsforskudd. Bidragsmottakeren mister retten til bidragsforskudd hvis barnet er plassert i fosterhjem eller kommunal eller statlig institusjon. NAV kan kreve tilbake bidragsforskudd som er feilutbetalt.\n" +
                        " \n" +
                        "Mer informasjon om underholdsbidrag/bidragsforskudd finner du på https://www.nav.no/no/Person/Familie/Barnebidrag \n" +
                        "\n" +
                        "[saksbehandler.enhet] \n" +
                        "", "Bidrag", "Utland", "Barnevern"),
                skrivestotteTekst("Ønsker kontakt med saksbehandler", "[bruker.navn] ønsker kontakt med saksbehandleren sin angående (TEKST). Jeg har gitt beskjed til saksbehandleren og opplyst om at svar kan ventes innen 2 virkedager.", "kontakt", "saksbehandler"),
                skrivestotteTekst("Levert søknad om sykepenger", "[bruker.navn] har levert søknad om sykepenger (del D) for perioden (FRADATO-TILDATO). NAV har mottatt søknaden og sender den videre for behandling. Jeg har opplyst om at behandlingstiden er på (ANTALL) uker.", "sykepenger", "søknad", "oppmøte"),
                skrivestotteTekst("Utsette utlevering ", "Hei [bruker.fornavn]\n" +
                        "Det er mulig å utsette utleveringen av hjelpemidler. Ta kontakt med hjelpemiddelsentralen på forhånd, så avtaler vi leveringstidspunkt.  \n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "utsette", "hms"),
                skrivestotteTekst("Innlevering av kassebil", "Hei [bruker.fornavn] \n" +
                        "NAV gjenbruker kassebiler med heis eller rampe som er mindre enn fem år gamle og som har kort kjørelengde. Disse skal derfor alltid innleveres. Du kan kjøpe ut biler som er eldre enn fem år. Ta kontakt med bilsenteret for å avtale dette på https://www.nav.no/no/Person/Hjelpemidler/Tjenester+og+produkter/Relatert+informasjon/Regionale+bilsentre.358771.cms\n" +
                        "", "hjelpemiddelsentralen", "hjelpemidler", "bil", "hms"),
                skrivestotteTekst("Samværsavtale overholdes ikke", "Hei [bruker.fornavn]\n" +
                        "Dersom en av partene hevder at avtalen eller offentlig fastsatt samvær eller rettsforlik ikke blir overholdt, må den som setter fram påstanden, klart bevise at dette er tilfelle. Bevisene må komme fra nøytralt hold. I praksis vil det si fra offentlige eller private instanser som barnehage, skole og lignende. Et eksempel kan være der bidragsmottakeren legger fram dokumentasjon fra barnehagen eller skolen om at det er hun/han som kjører og henter barnet i barnehagen eller på skolen når  barnet i henhold til samværsavtalen/-avgjørelsen skulle vært hos den bidragspliktige. NAV Forvaltning må i alle tilfeller vurdere de opplysningene/bevisene som bidragsmottakeren legger fram.\n" +
                        "", "bidrag", "samvær"),
                skrivestotteTekst("Utgifter til underhold av barn - underholdskostnad", "Hei [bruker.fornavn]\n" +
                        "Utgiftene varierer med alderen til barnet. Foruten barnetrygd inngår\n" +
                        "- forbruksutgifter\n" +
                        "- boutgifter\n" +
                        "- tilsynsutgifter\n" +
                        "\n" +
                        "Utgiftene er fastsatt med utgangspunkt i Statens institutt for forbruksforsknings standardbudsjett. Dette budsjettet omfatter de viktigste forbruksområdene, og er ment å gi uttrykk for et nøkternt og rimelig forbruksnivå. Varene som danner grunnlag for beregningene, holder enkel god kvalitet og lav pris. Det er dessuten et langtidsbudsjett slik at det er lagt inn noe ekstra hver måned for å dekke mer sjeldne og større utgifter.\n" +
                        "", "bidrag"),
                skrivestotteTekst("Kan ikke møte som avtalt", "[bruker.navn] er innkalt til avtale med veileder den (DATO)(KLOKKESLETT) [bruker.fornavn] kan ikke møte på grunn av (SYKDOM /ARBEID / ANNEN ÅRSAK) og ber om nytt møtetidspunkt. \n" +
                        "\n" +
                        "Jeg har gitt beskjed til NAV-kontoret og informert om at [bruker.fornavn] kan forvente svar innen to virkedager. \n" +
                        "\n" +
                        "Jeg har informert om at (YTELSE) kan bli stanset hvis [bruker.fornavn] ikke møter opp, og at [bruker.fornavn] må sende svarslippen som lå ved innkallingen.", "ks"),
                skrivestotteTekst("Endre skattetrekk", "[bruker.navn] har fått veiledning i hvordan endre skattetrekk. Jeg har opplyst om at Skatteetaten må kontaktes for endring av skattetrekket.", "veiledning", "skatt", "skattetrekk"),
                skrivestotteTekst("Tidspunkt for utbetaling av forskudd", "Hei [bruker.fornavn]\n" +
                        "Når du oppfyller vilkårene for forskudd vil du normalt ha pengene på konto innen den 10. hver måned.\n" +
                        "", "bidrag", "innkreving", "utbetaling", "forskudd"),
                skrivestotteTekst("Ileggelse av gebyr", "Hei [bruker.fornavn]\n" +
                        "Fordi du er blitt ilagt dette i vedtaket om bidrag (se mer om dette i vedtaket). \n" +
                        "", "bidrag", "innkreving", "fastsettelsesgebyr"),
                skrivestotteTekst("Oversikt utbetaling av bidrag", "Hei [bruker.fornavn]\n" +
                        "Du finner en oversikt over utbetalinger ved å logge på Ditt NAV\n" +
                        "", "bidrag", "innkreving", "utbetaling"),
                skrivestotteTekst("Rett til å søke uføretrygd - Bosatt i EØS", "[saksbehandler.enhet] har kommet til at [bruker.navn] bør søke om uføretrygd på grunn av varig nedsatt arbeidsevne.\n" +
                        "\n" +
                        "Siden [bruker.fornavn] er bosatt i et EØS-land, og skal ha uføretrygd fra et annet EØS-land, må [bruker.fornavn] kontakte trygdemyndighetene i bostedslandet, og få deres hjelp til å sende uføresøknaden til NAV. [bruker.fornavn] må kontakte {AKTUELLE MYNDIGHETER}. De sender så søknaden videre til NAV. Søknadsblankettene heter E 204, E 205 og E 207.\n" +
                        "\n" +
                        "[bruker.fornavn] er gjort oppmerksom på at vi kan innvilge arbeidsavklaringspenger i inntil åtte måneder mens vi behandler uføresøknaden. Dette kan [bruker.fornavn] kun få, hvis vi mottar dokumentasjon på at [bruker.fornavn] har søkt om uføretrygd via myndighetene i bostedslandet. Dokumentasjonen skal helst være at [saksbehandler.enhet] har mottatt søknaden fra myndighetene i bostedslandet. Det kan også være en bekreftelse på at myndighetene i bostedslandet behandler søknaden.\n" +
                        "\n" +
                        "[bruker.fornavn] har fått beskjed om at NAV må ha mottatt dokumentasjon på ovennevnte søknad snarest, og senest innen {OPPGI FRIST}.\n" +
                        "\n" +
                        "Hvis NAV ikke mottar søknad om uføretrygd innen den oppgitte fristen, vil vi stanse arbeidsavklaringspengene.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "AAP", "Utland", "Uføre"),
                skrivestotteTekst("Aktivitetsplikt - aktivitetsplan", "I forbindelse med servicen vår til [bruker.navn] er det avtalt en telefonsamtale den {ANGI DAG OG TIDSPUNKT}.\n" +
                        "\n" +
                        "Det er aktivitetsplikt på arbeidsavklaringspenger, hvor målet er å beholde eller skaffe arbeid. Derfor skal vi, sammen med [bruker.fornavn], legge opp en aktivitetsplan. Før samtalen gjennomføres er det viktig at [bruker.fornavn] tenker gjennom følgende punkter:\n" +
                        "- Nåværende situasjon\n" +
                        "- Mål for aktivitet fremover\n" +
                        "\n" +
                        "Muligheter for å komme tilbake i arbeid:\n" +
                        "- Hos nåværende arbeidsgiver\n" +
                        "- Yrkesbakgrunn og kompetanse\n" +
                        "- Heltid eller deltid\n" +
                        "- Arbeidsmuligheter på hjemstedet og andre steder\n" +
                        "\n" +
                        "[bruker.fornavn] er informert om at aktiviteten hovedsakelig skal foregår i bostedslandet.\n" +
                        "\n" +
                        "Vi tar kontakt med [bruker.fornavn] på telefonnummer {ANGI TELEFONNUMMER TIL BRUKER, ELLER HENVIS TIL GJELDENDE KONTAKTOPPLYSNINGER}.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "AAP", "Utland", "TLF"),
                skrivestotteTekst("Medlemskap ved arbeid i utlandet", "[bruker.navn] har fått generell informasjon om medlemskap i folketrygden ved arbeid i utlandet. [bruker.fornavn] er veiledet til søknadsskjemaet på nav.no.\n" +
                        "\n" +
                        "[saksbehandler.enhet]\n" +
                        "", "Medlemskap", "Utland", "Arbeid")
        ));

        for (int i = 0; i < RANDOM_TEKSTER; i++) {
            tekster.add(skrivestotteTekst("Random tekst " + i, "Norsk tekst streng " + i, "random", "id" + i));
        }

        return tekster;
    }

    private static SkrivestotteTekst skrivestotteTekst(String tittel, String norsk, String... tags) {
        HashMap<String, String> innhold = new HashMap<>();
        innhold.put(SkrivestotteTekst.LOCALE_DEFAULT, norsk);
        return new SkrivestotteTekst(String.valueOf(key++), tittel, innhold, tags);
    }
}
