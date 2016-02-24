import React from 'react';

const behandlingskjedeTilJSDate = (kjede) => new Date(kjede.sistOppdatert);
const dokumentMetadataTilJSDate = (dokument) => new Date(dokument.dato.year, dokument.dato.monthValue - 1, dokument.dato.dayOfMonth);
const nyesteDokumentForst = (dok1, dok2) => dokumentMetadataTilJSDate(dok1) < dokumentMetadataTilJSDate(dok2) ? 1 : -1;
const nyesteBehandlingskjedeForst = (b1, b2) => behandlingskjedeTilJSDate(b1) < behandlingskjedeTilJSDate(b2) ? 1 : -1;

const sisteOppdatering = (nyesteDokument, nyesteBehandlingskjede) => {
    const harDokument = !!nyesteDokument;
    const harBehandlingskjede = !!nyesteBehandlingskjede;

    let sisteOppdateringISaken;

    if(harBehandlingskjede && !harDokument) {
        sisteOppdateringISaken = behandlingskjedeTilJSDate(nyesteBehandlingskjede);
    } else if(harDokument && !harBehandlingskjede) {
        sisteOppdateringISaken = dokumentMetadataTilJSDate(nyesteDokument);
    } else if(!nyesteBehandlingskjede && !nyesteDokument){
        sisteOppdateringISaken = null;
    } else {
        const sistOppdatertBehandlingskjedeDate = behandlingskjedeTilJSDate(nyesteBehandlingskjede);
        const sistOppdatertDokumentDate = dokumentMetadataTilJSDate(nyesteDokument);
        sisteOppdateringISaken = sistOppdatertDokumentDate > sistOppdatertBehandlingskjedeDate ? sistOppdatertDokumentDate : sistOppdatertBehandlingskjedeDate;
    }

    return sisteOppdateringISaken;
};

export const finnSisteOppdatering = (behandlingskjeder, dokumenter) => {
    const nyesteBehandlingskjede = behandlingskjeder.sort(nyesteBehandlingskjedeForst)[0];
    const nyesteDokument = dokumenter.sort(nyesteDokumentForst)[0];
    return sisteOppdatering(nyesteDokument, nyesteBehandlingskjede);
};
