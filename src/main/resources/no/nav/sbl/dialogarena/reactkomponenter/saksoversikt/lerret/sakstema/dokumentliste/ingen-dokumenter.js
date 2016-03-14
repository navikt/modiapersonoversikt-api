import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const IngenDokumenter = ({ ingenDokumenterHeader }) => {
    function openGosys(e) {
        e.preventDefault();
        document.querySelector('.hiddenGosysLenkePanel').click();
    }

    return (
        <div className="default-error ingendokumenter">{ingenDokumenterHeader}
            <p className="ingendokumenterforklaring">
                <FormattedMessage id="dokumentinfo.sakstema.ingen.dokumenter.forklaring"/>
            </p>
            <a href="javascript:void(0);" onClick={openGosys}>
                <FormattedMessage id="dokumentinfo.sakstema.lenke.gosys"/>
            </a>
        </div >);
};


IngenDokumenter.propTypes = {
    ingenDokumenterHeader: pt.object.isRequired
};

export default IngenDokumenter;
