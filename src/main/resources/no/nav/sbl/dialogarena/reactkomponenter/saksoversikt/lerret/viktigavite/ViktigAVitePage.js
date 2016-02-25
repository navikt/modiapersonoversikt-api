import React from 'react';

class ViktigAVitePage extends React.Component {
    render() {
        const { tekster, valgtTema, sakstema, fnr } = this.props;

        const valgttema = "DAG";
        const temanavn = sakstema.filter(tema => tema.temakode === valgttema)[0].temanavn;
        const tittel = "Viktig å vite"; //tekster['saksinformasjon.tittel'];
        const innhold = "Innhold"; //tekster[`saksinformasjon.${valgttema}`];

        const tilbakelenke = `/modiabrukerdialog/person/${fnr}?temakode=${valgttema}#!saksoversikt`;
        return (
            <div className="viktigavitepanel side-innhold">
                <div className="blokk-s">
                    <a href={tilbakelenke}>Tilbake til sakstema</a>
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

export const skalViseViktigAViteSideForTema = (miljovariabler, tema) => {
    return miljovariabler['temasider.viktigavitelenke'].split(',').some(temaMedViktigAVite => temaMedViktigAVite === tema);
};

function createMarkup(markuptekst) {
    return {
        __html: markuptekst
    };
};

export default ViktigAVitePage;

