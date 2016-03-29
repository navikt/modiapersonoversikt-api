import React, { PropTypes as PT } from 'react';

export const skalViseViktigAViteSideForTema = (godkjenteTemakoder, valgtTemakode) =>
    godkjenteTemakoder.split(',').some(temaMedViktigAVite => temaMedViktigAVite === valgtTemakode);


class ViktigAViteLenke extends React.Component {
    constructor() {
        super();
        this._redirect = this._redirect.bind(this);
    }

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('viktigavite');
    }

    render() {
        const { valgtTema } = this.props;
        const { miljovariabler } = this.context;

        if (skalViseViktigAViteSideForTema(miljovariabler['temasider.viktigavitelenke'], valgtTema.temakode)) {
            const linktekst = `Viktig å vite om ${valgtTema.temanavn}`;
            return (
                <div className="viktig-aa-vite-container">
                    <div className="viktig-aa-vite-lenke">
                        <a href="javascript:void(0);" onClick={this._redirect}>{linktekst}</a>
                    </div>
                </div>
            );
        }
        return <noscript/>;
    }
}


ViktigAViteLenke.propTypes = {
    valgtTema: PT.object.isRequired,
    visSide: PT.func.isRequired
};

ViktigAViteLenke.contextTypes = {
    miljovariabler: PT.object.isRequired
};

export default ViktigAViteLenke;
