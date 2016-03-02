export const dokumentMetadataTilJSDate = (dato) => new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth);
export const javaLocalDateTimeToJSDate = (dato) => new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth, dato.hour, dato.minute, dato.second);


export const datoformat = {
    NUMERISK_KORT: {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    }
};