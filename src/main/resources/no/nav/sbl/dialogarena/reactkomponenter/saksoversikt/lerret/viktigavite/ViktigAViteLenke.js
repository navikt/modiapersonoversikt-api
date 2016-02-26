import React from 'react';

class ViktigAViteLenke extends React.Component {
    _redirect(e) {
        e.preventDefault();
        this.props.visSide('viktigavite');
    }

    render() {
        const { valgtTema } = this.props;

        if (skalViseViktigAViteSideForTema(valgtTema.temakode)) {
            const linktekst = `Viktig å vite om ${valgtTema.temanavn}`;
            return (
                <div className="listepanel">
                    <a href="javascript:void(0);" onClick={this._redirect.bind(this)}>{linktekst}</a>
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

