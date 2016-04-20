export const javaLocalDateTimeToJSDate = (dato) =>
    new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth, dato.hour, dato.minute, dato.second);

export const fromDateToJSON = (date) =>
    ({
        chronology: {
            calendarType: 'iso8601',
            id: 'ISO'
        },
        dayOfMonth: date.getDate(),
        dayOfWeek: date.getDay(),
        hour: 0,
        minute: 0,
        monthValue: date.getMonth() + 1,
        nano: 0,
        second: 0,
        year: date.getFullYear(),
        dayOfYear: date.getDate()
    });

export const datoformat = {
    NUMERISK_KORT: {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    },
    NUMERISK_2_DIGIT: {
        day: '2-digit',
        month: '2-digit',
        year: '2-digit'
    }
};
