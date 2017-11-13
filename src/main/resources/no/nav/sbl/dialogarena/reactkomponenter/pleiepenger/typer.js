import PT from 'prop-types';

export const javaDatoType = PT.shape({
    year: PT.number.isRequired,
    monthValue: PT.number.isRequired,
    dayOfMonth: PT.number.isRequired
});
