import React from 'react';
import { FormattedMessage } from 'react-intl';
import { javaLocalDateTimeToJSDate } from './dato-utils';

const finnTekst = (antallUnderBehandling, antallFerdigBehandlet, langTekst) => {
    const tallSomTekstUnderBehandling = <FormattedMessage id="behandlingsstatus.telling" values={{antall: antallUnderBehandling}}/>;
    const tallSomTekstFerdigBehandlet = <FormattedMessage id="behandlingsstatus.telling" values={{antall: antallFerdigBehandlet}}/>;
    const values = {
        antallSoknaderUnderBehandling: tallSomTekstUnderBehandling,
        antallSoknaderFerdigBehandlet: tallSomTekstFerdigBehandlet
    };
    let key;
    if (antallUnderBehandling > 0 && antallFerdigBehandlet > 0) {
        key = langTekst ? 'behandlingsstatus.under.og.ferdig.behandlet.lang' : 'behandlingsstatus.under.og.ferdig.behandlet';
    } else if (antallUnderBehandling > 0) {
        key = langTekst ? 'behandlingsstatus.under.behandling.lang' : 'behandlingsstatus.underbehandling';
    } else if (antallFerdigBehandlet > 0) {
        key = langTekst ? 'behandlingsstatus.ferdig.behandlet.lang' : 'behandlingsstatus.ferdigbehandlet';
    }

    return key ? <FormattedMessage id={key} values={values}/> : null;
};

const datoNyereEnnAntallDager = (date, antallDager) => {
    const grense = new Date();
    grense.setDate(grense.getDate() - antallDager);
    return date >= grense;
};

const underBehandlingEllerNyereEnnGrenseverdi = (antallDager) => (behandlingskjede) => {
    return behandlingskjede.status === 'UNDER_BEHANDLING' || datoNyereEnnAntallDager(new Date(behandlingskjede.sistOppdatert.millis), antallDager);
};

const behandlingskjedeTilJSDate = (kjede) => new Date(kjede.sistOppdatert.millis);
const sluttbrukerEllerTredjepart = (dokument) => dokument.avsender === 'SLUTTBRUKER' ? tekster('dokumentinfo.deg') : dokument.navn;
const fjernEldreEnn = (antallDager) => (dokument) => datoNyereEnnAntallDager(javaLocalDateTimeToJSDate(dokument.dato), antallDager);
const nyesteDokumentForst = (dok1, dok2) => javaLocalDateTimeToJSDate(dok1.dato) < javaLocalDateTimeToJSDate(dok2.dato) ? 1 : -1;

//const nyesteBehandlingskjedeForst = (b1, b2) => new Date(b1.sistOppdatert.millis) < new Date(b2.sistOppdatert.millis) ? 1 : -1;
const nyesteBehandlingskjedeForst = (b1, b2) => javaLocalDateTimeToJSDate(b1.sistOppdatert) < javaLocalDateTimeToJSDate(b2.sistOppdatert) ? 1 : -1;



const tilDokumentinfoTekst = (dokument) => {
    const values = {
        sak: dokument.hoveddokument.tittel,
        dato: javaLocalDateTimeToJSDate(dokument.dato),
        avsender: sluttbrukerEllerTredjepart(dokument)
    };

    const keyBasertPaaRetning = {
        INN: 'dokumentinfo.inngaaende',
        UT: 'dokumentinfo.utgaaende',
        INTERN: 'dokumentinfo.samtalereferat'
    };

    return <FormattedMessage id={keyBasertPaaRetning[dokument.retning]} values={values}/>;
};

const sisteOppdatering = (nyesteDokument, nyesteBehandlingskjede) => {
    const harDokument = !!nyesteDokument;
    const harBehandlingskjede = !!nyesteBehandlingskjede;

    let sisteOppdateringISaken;

    if(harBehandlingskjede && !harDokument) {
        //sisteOppdateringISaken = behandlingskjedeTilJSDate(nyesteBehandlingskjede);
        sisteOppdateringISaken = javaLocalDateTimeToJSDate(nyesteBehandlingskjede.sistOppdatert);
    } else if(harDokument && !harBehandlingskjede) {
        sisteOppdateringISaken = javaLocalDateTimeToJSDate(nyesteDokument.dato);
    } else if(!nyesteBehandlingskjede && !nyesteDokument){
        sisteOppdateringISaken = null;
    } else {
        //const sistOppdatertBehandlingskjedeDate = behandlingskjedeTilJSDate(nyesteBehandlingskjede);
        const sistOppdatertBehandlingskjedeDate = javaLocalDateTimeToJSDate(nyesteBehandlingskjede.sistOppdatert);
        const sistOppdatertDokumentDate = javaLocalDateTimeToJSDate(nyesteDokument.dato);
        sisteOppdateringISaken = sistOppdatertDokumentDate > sistOppdatertBehandlingskjedeDate ? sistOppdatertDokumentDate : sistOppdatertBehandlingskjedeDate;
    }

    return sisteOppdateringISaken;
};

export const finnBehandlingsstatus = (behandlingskjeder, antallDagerFerdigBehandletStatusErGyldig, langTekst) => {
    const gyldigeBehandlingskjeder = behandlingskjeder.filter(underBehandlingEllerNyereEnnGrenseverdi(antallDagerFerdigBehandletStatusErGyldig));
    const antallUnderBehandling = gyldigeBehandlingskjeder.filter(kjede => kjede.status === 'UNDER_BEHANDLING').length;
    const antallFerdigBehandlet = gyldigeBehandlingskjeder.filter(kjede => kjede.status === 'FERDIG_BEHANDLET').length;
    return finnTekst(antallUnderBehandling, antallFerdigBehandlet, langTekst);
};

export const finnNokkelinfoForSakstema = (behandlingskjeder, dokumenter, antallDagerFerdigBehandletStatusErGyldig, langBehandlingsstatusTekst = false) => {
    const sorterteBehandlingskjeder = behandlingskjeder.sort(nyesteBehandlingskjedeForst);
    const nyesteBehandlingskjede = sorterteBehandlingskjeder[0];

    const behandlingsstatus = finnBehandlingsstatus(sorterteBehandlingskjeder, antallDagerFerdigBehandletStatusErGyldig, langBehandlingsstatusTekst);

    const sorterteDokumenter = dokumenter.sort(nyesteDokumentForst);
    const nyesteDokument = sorterteDokumenter[0];

    return {
        behandlingsstatus: behandlingsstatus,
        sisteOppdatering: sisteOppdatering(nyesteDokument, nyesteBehandlingskjede)
    };
};
