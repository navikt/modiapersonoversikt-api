import PT from 'prop-types';

export const TraadBegrep = PT.shape({
    entall: PT.string.isRequired,
    bestemtEntall: PT.string.isRequired,
    flertall: PT.string.isRequired
});
