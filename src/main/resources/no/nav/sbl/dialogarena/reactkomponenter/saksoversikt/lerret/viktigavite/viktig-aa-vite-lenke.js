import React from 'react';
import PT from 'prop-types';
import {connect} from 'react-redux';

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
        const { valgtTema, temakodeliste } = this.props;
        const { miljovariabler } = this.context;

        let temaer = null;
        if ( valgtTema.temakode === 'alle') {
            temaer = temakodeliste.join();
        } else {
            temaer = valgtTema.temakode;
        }

        let norglenke  = `${this.props.norgUrl}/#/startsok?tema=${temaer}`;
        if ( this.props.gt != null ) {
            norglenke += `&gt=${this.props.gt}`;
        }
        if( this.props.diskresjonskode === 'SPSF') {
            norglenke += `&disk=${this.props.diskresjonskode}`;
        }

        const linktekst = `Viktig å vite om ${valgtTema.temanavn}`;
        const lenke = <li><a href="javascript:void(0);" onClick={this._redirect}>{linktekst}</a></li>;
        const skalViselenke = skalViseViktigAViteSideForTema(miljovariabler['temasider.viktigavitelenke'], valgtTema.temakode);
        return (
            <div className="viktig-aa-vite-container">
                <div className="viktig-aa-vite-lenke">
                    <ul>
                        <li><a href={norglenke} target="_blank">Oversikt med enheter og tema de behandler</a></li>
                        {skalViselenke && lenke}
                    </ul>
                </div>
            </div>
        );
    }
}


ViktigAViteLenke.propTypes = {
    valgtTema: PT.object.isRequired,
    temakodeliste: PT.arrayOf(PT.string),
    visSide: PT.func.isRequired,
    gt: PT.string.isRequired,
    diskresjonskode: PT.string,
    norgUrl: PT.string.isRequired
};

ViktigAViteLenke.defaultProps = {
    temakodeliste: []
}

ViktigAViteLenke.contextTypes = {
    miljovariabler: PT.object.isRequired
};

const mapStateToProps = state => ({
    temakodeliste: state.lerret.temakodeliste
});
export default connect(mapStateToProps)(ViktigAViteLenke);
