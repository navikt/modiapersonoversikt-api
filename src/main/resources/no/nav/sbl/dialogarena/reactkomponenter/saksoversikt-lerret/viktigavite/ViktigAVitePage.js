import React from 'react';

class ViktigAVitePage extends React.Component {
    render() {

        const props = this.props.store.state;
        const valgttema = props.valgtTema;
        const temanavn = props.sakstema.filter(tema => tema.temakode === valgttema)[0].temanavn;

        const tekster = props.tekster;

        const tittel = tekster['saksinformasjon.tittel'];
        const innhold = tekster[`saksinformasjon.${valgttema}`];

        const tilbakelenke = `/modiabrukerdialog/person/${props.fnr}?temakode=${valgttema}#!saksoversikt`;
        return (
            <div className="viktigavitepanel side-innhold">
                <div className="blokk-s">
                    <a href={tilbakelenke}>{tekster['tilbake.viktigavite']}</a>
                </div>
                <panel className="panel">
                    <h1 className="decorated typo-innholdstittel">{tittel} - {temanavn}</h1>
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

export default ViktigAVitePage;

