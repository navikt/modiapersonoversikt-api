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
        setTimeout(() => {
            this.refs.viktigaviteOverskrift.focus();
        }, 0);
    }

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('sakstema');
    }

    render() {
        const { valgtTema, sakstema } = this.props;
        const temanavn = sakstema.find(tema => tema.temakode === valgtTema.temakode).temanavn;
        const sidetittel = <FormattedMessage id="saksinformasjon.vikigavite.tittel" values={{ tema: temanavn }}/>;
        const innhold = this.props.intl.formatMessage({ id: `saksinformasjon.${valgtTema.temakode}` });

        return (
            <section className="viktig-a-vite-page">
                <div className="fixed-header blokk-s">
                    <a href="#" onClick={this._redirect} className="close-document"></a>
                </div>
                <panel className="panel scrollpanel side-innhold">
                    <h1 ref="viktigaviteOverskrift" className="decorated typo-innholdstittel" tabIndex="-1">{sidetittel}</h1>
                    <article>
                        <div dangerouslySetInnerHTML={createMarkup(innhold)}/>
                    </article>
                </panel>
            </section>
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
