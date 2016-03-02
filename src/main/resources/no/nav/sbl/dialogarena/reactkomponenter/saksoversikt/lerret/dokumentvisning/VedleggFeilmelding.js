import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';


class VedleggFeilmelding extends React.Component {
    render() {
        const { feilmelding: { bildeUrl, feilmeldingEnonicKey, ekstrafeilinfo } } = this.props;

        const enonicFeilmeldingstekstKey = feilmeldingEnonicKey.concat('.tekst');
        const innhold =  this.props.intl.formatMessage({id:enonicFeilmeldingstekstKey}, ekstrafeilinfo);

        return (
            <div className="feilmelding-container">
                <img className="feilmelding-bakgrunn" src={bildeUrl} alt=""/>
                <div className="feilmelding panel panel-ramme">
                    <h1 className="vanlig-ikon-feil-strek"><FormattedMessage id={feilmeldingEnonicKey.concat('.tittel')} /></h1>
                    <p className="text-center" dangerouslySetInnerHTML={createMarkup(innhold)}/>
                </div>
            </div>
        )
    }
}


function createMarkup(markuptekst) {
    return {
        __html: markuptekst
    };
};


VedleggFeilmelding.propTypes = {
    feilmelding: React.PropTypes.shape({
        feilmeldingEnonicKey: React.PropTypes.string.isRequired,
        kanVises: React.PropTypes.bool.isRequired,
        ekstrafeilinfo: React.PropTypes.object,
        bildeUrl: React.PropTypes.string.isRequired
    }).isRequired
};

export default injectIntl(VedleggFeilmelding);