import { javaLocalDateTimeToJSDate } from './dato-utils';

const nyesteElementISakstema = (sakstema) => {
    const behandlingskjeder = sakstema.behandlingskjeder.map((behandlingskjede) => javaLocalDateTimeToJSDate(behandlingskjede.sistOppdatert).getTime());
    const metadata = sakstema.dokumentMetadata.map((metadata) => javaLocalDateTimeToJSDate(metadata.dato).getTime());

    const combined = [].concat(behandlingskjeder, metadata);

    if (combined.length === 0) return -1;
    combined.sort().reverse();
    return combined[0];
};

export const nyesteSakstema = (saktema1, saktema2) => {
    return nyesteElementISakstema(saktema2) - nyesteElementISakstema(saktema1);
};