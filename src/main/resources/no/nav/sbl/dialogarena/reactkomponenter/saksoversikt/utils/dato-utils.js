export const dokumentMetadataTilJSDate = (dato) => new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth);
export const javaLocalDateTimeToJSDate = (dato) => new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth, dato.hour, dato.minute, dato.second);
