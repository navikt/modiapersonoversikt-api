import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';

class ViktigAVitePage extends React.Component {
    _redirect(e) {
        e.preventDefault();
        this.props.visSide('sakstema');
    }
    render() {
        const { valgtTema, sakstema } = this.props;

        const temanavn = sakstema.find(tema => tema.temakode === valgtTema.temakode).temanavn;
        const sidetittel = <FormattedMessage id="saksinformasjon.vikigavite.tittel" values={{tema: temanavn}}/>;
        const innhold =  this.props.intl.formatMessage({id:`saksinformasjon.${valgtTema.temakode}`});

        //TODO A-lenka forårsaker full re-rendering av Modia. Må se på alternativ løsning
        return (
            <div className="viktigavitepanel side-innhold">
                <div className="blokk-s">
                    <a href="javascript:void(0);" onClick={this._redirect.bind(this)}>Tilbake til sakstema</a>
                </div>
                <panel className="panel">
                    <h1 className="decorated typo-innholdstittel">{sidetittel}</h1>
                    <section>
                        <div dangerouslySetInnerHTML={createMarkup(innhold)}/>
                    </section>
                </panel>
            </div>
        )
    };
}

function createMarkup(markuptekst) {
    return {
        __html: markuptekst
    };
};

export default injectIntl(ViktigAVitePage);

