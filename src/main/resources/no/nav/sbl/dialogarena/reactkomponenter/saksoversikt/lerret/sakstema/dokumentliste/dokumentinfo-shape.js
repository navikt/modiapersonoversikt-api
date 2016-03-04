import { PropTypes as pt } from 'react';

export default pt.shape({
    retning: pt.string.isRequired,
        avsender: pt.string.isRequired,
        mottaker: pt.string.isRequired,
        navn: pt.string,
        hoveddokument: pt.object.isRequired,
        vedlegg: pt.array,
        temakodeVisning: pt.string,
        feilWrapper: pt.object.isRequired
});