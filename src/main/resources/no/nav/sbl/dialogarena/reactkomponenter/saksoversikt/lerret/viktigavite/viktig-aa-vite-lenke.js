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
        console.log('VAVLenke');
        console.log(this.props);
        const { valgtTema, temakodeliste } = this.props;
        const { miljovariabler } = this.context;

        let temaer = null;
        if ( valgtTema.temakode === 'alle') {
            temaer = temakodeliste.join();
        } else {
            temaer = valgtTema.temakode;
        }

        let norglenke  = `${this.props.norgUrl}/#/startsok?gt=${this.props.gt}&tema=${temaer}`;
        if( this.props.diskresjonskode === 'SPSF') {
            norglenke += `&disk=${this.props.diskresjonskode}`;
        }

        console.log('VAVtemakodeliste', temakodeliste);

        const linktekst = `Viktig å vite om ${valgtTema.temanavn}`;
        const lenke = <a href="javascript:void(0);" onClick={this._redirect}>{linktekst}</a>;
        const skalViselenke = skalViseViktigAViteSideForTema(miljovariabler['temasider.viktigavitelenke'], valgtTema.temakode);

        console.log(skalViselenke);
        return (
            <div className="viktig-aa-vite-container">
                <div className="viktig-aa-vite-lenke">
                    <a href={norglenke} target="_blank">dummytekst Arbeidsfordeling i NORG</a>
                    <div>
                        {skalViselenke && lenke}
                    </div>
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
