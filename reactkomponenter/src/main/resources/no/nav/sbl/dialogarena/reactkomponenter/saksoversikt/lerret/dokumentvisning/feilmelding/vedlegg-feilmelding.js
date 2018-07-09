import React from 'react';
import PT from 'prop-types';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { Element } from 'react-scroll';

function createMarkup(markuptekst) {
    return {
        __html: markuptekst
    };
}

class VedleggFeilmelding extends React.Component {
    render() {
        const { feilmelding: { bildeUrl, feilmeldingEnonicKey, ekstrafeilinfo }, name } = this.props;

        const enonicFeilmeldingstekstKey = feilmeldingEnonicKey.concat('.tekst');
        const innhold = this.props.intl.formatMessage({ id: enonicFeilmeldingstekstKey }, ekstrafeilinfo);

        return (
            <Element className="feilmelding-container" name={name}>
                <article>
                    <img className="feilmelding-bakgrunn" src={bildeUrl} alt="" />
                    <div className="feilmelding panel panel-ramme">
                        <h1 className="vanlig-ikon-feil-strek">
                            <FormattedMessage id={feilmeldingEnonicKey.concat('.tittel')} />
                        </h1>
                        <p className="text-center" dangerouslySetInnerHTML={createMarkup(innhold)} />
                    </div>
                </article>
            </Element>
        );
    }
}

VedleggFeilmelding.propTypes = {
    feilmelding: PT.shape({
        feilmeldingEnonicKey: PT.string.isRequired,
        kanVises: PT.bool.isRequired,
        ekstrafeilinfo: PT.object,
        bildeUrl: PT.string.isRequired
    }).isRequired,
    name: PT.string,
    intl: intlShape
};

export default injectIntl(VedleggFeilmelding);
