import React from 'react';

class ViktigAVitePage extends React.Component {
    render() {
        const { valgtTema, fnr, sakstema } = this.props;

        const temanavn = sakstema.filter(tema => tema.temakode === valgtTema)[0].temanavn;
        //TODO Tekster fra enonic. Avventer react-intl
        const titteltekst = "Viktig å vite"; //tekster['saksinformasjon.tittel'];
        const innhold = "Innhold"; //tekster[`saksinformasjon.${valgttema}`];

        const tilbakelenke = `/modiabrukerdialog/person/${fnr}?temakode=${valgtTema}#!saksoversikt`;
        const sidetittel  = `${titteltekst} - ${temanavn}`;

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

export default ViktigAVitePage;

