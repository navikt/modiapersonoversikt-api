import React from 'react';

class ViktigAViteLenke extends React.Component {
    render() {
        const { valgtTema, fnr } = this.props;

        if (skalViseViktigAViteSideForTema(valgtTema.temakode)) {
            const linktekst = `Viktig å vite om ${valgtTema.temanavn}`;
            return (
                <div className="listepanel">
                    <a href={`/modiabrukerdialog/person/${fnr}?temakode=${valgtTema.temakode}&valgtside=viktigavite#!saksoversikt`}>{linktekst}</a>
                </div>
            );
        } else {
            return (
                <div/>
            );
        }

    };
}

//TODO Dette må hentes fra miljovariabler
export const skalViseViktigAViteSideForTema = (tema) => {
    return "DAG,AAP,IND".split(',').some(temaMedViktigAVite => temaMedViktigAVite === tema);
};

export default ViktigAViteLenke;

