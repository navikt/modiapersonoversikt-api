import React, { PropTypes as PT } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';

function createMarkup(markuptekst) {
    return {
        __html: markuptekst
    };
}

class ViktigAVitePage extends React.Component {
    constructor() {
        super();
        this._redirect = this._redirect.bind(this);
    }


    componentDidMount() {
        setTimeout(() => this.refs.viktigaviteOverskrift && this.refs.viktigaviteOverskrift.focus(), 0);
    }

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('sakstema');
    }

    render() {
        const { intl, valgtTema, sakstema } = this.props;
        const temanavn = sakstema.find(tema => tema.temakode === valgtTema.temakode).temanavn;
        const sidetittel = <FormattedMessage id="saksinformasjon.vikigavite.tittel" values={{ tema: temanavn }} />;
        const innhold = this.props.intl.formatMessage({ id: `saksinformasjon.${valgtTema.temakode}` });

        return (
            <div className="viktig-a-vite-page">
                <div className="fixed-header blokk-s">
                    <button
                        onClick={this._redirect}
                        className="close-document"
                        type="button"
                        aria-label={intl.formatMessage({ id: 'dokumentvisning.aria.lukk' })}
                        title={intl.formatMessage({ id: 'dokumentvisning.aria.lukk' })}
                    >
                    </button>
                </div>
                <section aria-labelledby="viktigaviteOverskrift" className="panel scrollpanel side-innhold">
                    <h1
                        ref="viktigaviteOverskrift"
                        id="viktigaviteOverskrift"
                        className="decorated typo-innholdstittel ikke-fokusmarkering"
                        tabIndex="-1"
                    >{sidetittel}</h1>
                    <article>
                        <div dangerouslySetInnerHTML={createMarkup(innhold)} />
                    </article>
                </section>
            </div>
        );
    }
}

ViktigAVitePage.propTypes = {
    intl: PT.object.isRequired,
    valgtTema: PT.object.isRequired,
    visSide: PT.func.isRequired,
    sakstema: PT.array.isRequired
};

export default injectIntl(ViktigAVitePage);
