import React, { PropTypes as PT } from 'react';
import Sakstema from './Sakstema';

const sakstemaComparator = (sakstema1, sakstema2) => {
    if (!sakstema1.hasOwnProperty('sistOppdatertDato')) {
        return 1;
    } else {
        return new Date(sakstema2.sistOppdatertDato) - new Date(sakstema1.sistOppdatertDato);
    }
};


class SakstemaListe extends React.Component {
    _lagAlleTema(temaliste) {
        return [{
            temanavn: 'Alle temaer',
            temakode: 'alle',
            dokumentmetadata: (temaliste[0] || {}).dokumentmetadata
        }];
    }

    render() {
        const temaListe = this.props.sakstema.sort(sakstemaComparator);

        const temalisteelementer = this._lagAlleTema(temaListe)
            .concat(temaListe)
            .map((tema) => (
                <Sakstema tema={tema} velgSak={this.props.velgSak} valgtTema={this.props.valgtTema}/>
            ));

        console.log('temalisteelementer', temalisteelementer);

        return (
            <div className="sakstemaliste">
                {temalisteelementer}
            </div>
        );
    }
}


Sakstema.propTypes = {
    sakstema: PT.array.isRequired,
    velgSak: PT.func.isRequired
};

export default SakstemaListe;
