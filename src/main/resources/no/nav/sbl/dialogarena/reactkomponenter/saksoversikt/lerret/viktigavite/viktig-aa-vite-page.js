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
        document.querySelector('.saksoversikt .lamellhode a').focus();
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
            <div className="grattpanel side-innhold viktigtaavitepanel">
                <div className="blokk-s">
                    <a role="button" href="javascript:void(0);" onClick={this._redirect}>Tilbake til sakstema</a>
                </div>
                <panel className="panel">
                    <h1 className="decorated typo-innholdstittel">{sidetittel}</h1>
                    <section>
                        <div dangerouslySetInnerHTML={createMarkup(innhold)}/>
                    </section>
                </panel>
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
