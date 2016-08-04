import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const IngenDokumenterBidrag = ({ ingenDokumenterHeader }) => {
    function openGosys(e) {
        e.preventDefault();
        document.querySelector('.hiddenGosysLenkePanel').click();
    }

    return (
        <div className="default-error ingendokumenter">
            {ingenDokumenterHeader}
            <p className="ingendokumenterforklaring">
                <FormattedMessage id="dokumentinfo.sakstema.ingen.dokumenter.bidrag" />
            </p>
            <a href="#" onClick={openGosys}>
                <FormattedMessage id="dokumentinfo.sakstema.lenke.gosys" />
            </a>
        </div>);
};

IngenDokumenterBidrag.propTypes = {
    ingenDokumenterHeader: pt.element.isRequired
};

export default IngenDokumenterBidrag;
