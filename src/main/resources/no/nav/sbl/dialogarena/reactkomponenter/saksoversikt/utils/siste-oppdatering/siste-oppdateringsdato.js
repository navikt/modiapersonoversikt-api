import { javaLocalDateTimeToJSDate } from './../dato-utils';

export const sisteOppdatering = (nyesteDokument, nyesteBehandlingskjede) => {
    const harDokument = !!nyesteDokument;
    const harBehandlingskjede = !!nyesteBehandlingskjede;

    let sisteOppdateringISaken;

    if (harBehandlingskjede && !harDokument) {
        sisteOppdateringISaken = javaLocalDateTimeToJSDate(nyesteBehandlingskjede.sistOppdatert);
    } else if (harDokument && !harBehandlingskjede) {
        sisteOppdateringISaken = javaLocalDateTimeToJSDate(nyesteDokument.dato);
    } else if (!nyesteBehandlingskjede && !nyesteDokument) {
        sisteOppdateringISaken = null;
    } else {
        const sistOppdatertBehandlingskjedeDate = javaLocalDateTimeToJSDate(nyesteBehandlingskjede.sistOppdatert);
        const sistOppdatertDokumentDate = javaLocalDateTimeToJSDate(nyesteDokument.dato);
        sisteOppdateringISaken = sistOppdatertDokumentDate > sistOppdatertBehandlingskjedeDate ?
            sistOppdatertDokumentDate : sistOppdatertBehandlingskjedeDate;
    }

    return sisteOppdateringISaken;
};
