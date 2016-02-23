import React from 'react';

class ViktigAVitePage extends React.Component {
    render() {


        const props = this.props.store.state;
        const temakode = props.valgtTema;
        const temanavn = props.sakstema.filter(tema => tema.temakode === temakode)[0].temanavn;

        const tekster = props.tekster;

        const tittel = tekster['saksinformasjon.tittel'];
        const innhold = tekster[`saksinformasjon.${temakode}`] ;

        return (
            <div>
                <h1>{tittel} - {temanavn}</h1>
                <section>
                    <div dangerouslySetInnerHTML={createMarkup(innhold)}/>
                </section>
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

