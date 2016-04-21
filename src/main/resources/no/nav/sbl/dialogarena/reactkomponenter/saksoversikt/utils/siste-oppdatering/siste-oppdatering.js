import { javaLocalDateTimeToJSDate } from './../dato-utils';
import { finnBehandlingsstatus } from './behandlingsstatus';
import { sisteOppdatering } from './siste-oppdateringsdato';

export const nyesteDokumentForst = (dok1, dok2) =>
    javaLocalDateTimeToJSDate(dok1.dato) < javaLocalDateTimeToJSDate(dok2.dato) ? 1 : -1;
const nyesteBehandlingskjedeForst = (b1, b2) =>
    javaLocalDateTimeToJSDate(b1.sistOppdatert) < javaLocalDateTimeToJSDate(b2.sistOppdatert) ? 1 : -1;


export const finnNokkelinfoForSakstema = (behandlingskjeder, dokumenter, antallDagerFerdigBehandletStatusErGyldig) => {
    const sorterteBehandlingskjeder = behandlingskjeder.sort(nyesteBehandlingskjedeForst);
    const nyesteBehandlingskjede = sorterteBehandlingskjeder[0];

    const behandlingsstatus =
        finnBehandlingsstatus(sorterteBehandlingskjeder, antallDagerFerdigBehandletStatusErGyldig);

    const sorterteDokumenter = dokumenter.sort(nyesteDokumentForst);
    const nyesteDokument = sorterteDokumenter[0];

    return {
        behandlingsstatus,
        sisteOppdatering: sisteOppdatering(nyesteDokument, nyesteBehandlingskjede)
    };
};
