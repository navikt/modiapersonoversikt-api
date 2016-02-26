import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';

class ViktigAVitePage extends React.Component {
    render() {
        const { valgtTema, fnr, sakstema } = this.props;

        const temanavn = sakstema.filter(tema => tema.temakode === valgtTema)[0].temanavn;
        const sidetittel = <FormattedMessage id="saksinformasjon.vikigavite.tittel" values={{tema: temanavn}}/>;
        const innhold =  this.props.intl.formatMessage({id:`saksinformasjon.${valgtTema}`});

        const tilbakelenke = `/modiabrukerdialog/person/${fnr}?temakode=${valgtTema}#!saksoversikt`;

        //TODO A-lenka forårsaker full re-rendering av Modia. Må se på alternativ løsning
        return (
            <div className="viktigavitepanel side-innhold">
                <div className="blokk-s">
                    <a href={tilbakelenke}>Tilbake til sakstema</a>
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

