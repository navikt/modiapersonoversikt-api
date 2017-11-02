import PT from 'prop-types';

export default PT.shape({
    retning: PT.string.isRequired,
    avsender: PT.string.isRequired,
    mottaker: PT.string.isRequired,
    navn: PT.string,
    hoveddokument: PT.object.isRequired,
    vedlegg: PT.array,
    temakodeVisning: PT.string,
    feilWrapper: PT.object.isRequired
});
