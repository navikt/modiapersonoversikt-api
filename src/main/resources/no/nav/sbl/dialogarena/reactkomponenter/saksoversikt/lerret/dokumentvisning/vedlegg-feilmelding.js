import React from 'react';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { Element } from 'react-scroll';

function createMarkup(markuptekst) {
    return {
        __html: markuptekst
    };
}

class VedleggFeilmelding extends React.Component {

    componentDidMount() {
        const feilmeldingContainer = document.getElementsByClassName('feilmelding-container')[0];
        const feilmelding = document.getElementsByClassName('feilmelding')[0];

        const feilmeldingContainerWidth = feilmeldingContainer.offsetWidth;
        const feilmeldingWidth = feilmelding.offsetWidth;
        const leftPosition = (feilmeldingContainerWidth - feilmeldingWidth) / 2;

        feilmelding.style.left = `${leftPosition}px`;
    }
    render() {
        const { feilmelding: { bildeUrl, feilmeldingEnonicKey, ekstrafeilinfo }, name } = this.props;

        const enonicFeilmeldingstekstKey = feilmeldingEnonicKey.concat('.tekst');
        const innhold = this.props.intl.formatMessage({ id: enonicFeilmeldingstekstKey }, ekstrafeilinfo);

        return (
            <Element className="feilmelding-container" name={name}>
                <img className="feilmelding-bakgrunn" src={bildeUrl} alt=""/>
                <div className="feilmelding panel panel-ramme">
                    <h1 className="vanlig-ikon-feil-strek"><FormattedMessage
                      id={feilmeldingEnonicKey.concat('.tittel')}
                    />
                    </h1>
                    <p className="text-center" dangerouslySetInnerHTML={createMarkup(innhold)}/>
                </div>
            </Element>
        );
    }
}

VedleggFeilmelding.propTypes = {
    feilmelding: React.PropTypes.shape({
        feilmeldingEnonicKey: React.PropTypes.string.isRequired,
        kanVises: React.PropTypes.bool.isRequired,
        ekstrafeilinfo: React.PropTypes.object,
        bildeUrl: React.PropTypes.string.isRequired
    }).isRequired,
    name: React.PropTypes.string,
    intl: intlShape
};

export default injectIntl(VedleggFeilmelding);
