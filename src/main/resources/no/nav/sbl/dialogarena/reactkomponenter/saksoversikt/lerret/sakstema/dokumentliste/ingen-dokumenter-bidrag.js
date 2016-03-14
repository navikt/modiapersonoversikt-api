import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const IngenDokumenterBidrag = ({ ingenDokumenterHeader }) =>
        <div className="default-error ingendokumenter">
            {ingenDokumenterHeader}
            <p className="ingendokumenterforklaring">
                <FormattedMessage id="dokumentinfo.sakstema.ingen.dokumenter.bidrag"/>
            </p>
        </div>;


IngenDokumenterBidrag.propTypes = {
    ingenDokumenterHeader: pt.object.isRequired
};

export default IngenDokumenterBidrag;
