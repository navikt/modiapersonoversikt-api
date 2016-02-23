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
    _erValgt(tema) {
        return tema === this.props.valgtTema;
    }

    render() {
        const temaListe = this.props.sakstema.sort(sakstemaComparator);

        const temalisteelementer = temaListe.map((tema) => (
            <Sakstema tema={tema} velgSak={this.props.velgSak}/>
        ));

        //<Sakstema tema="Alle temaer"
        //          temakode={"alle"}
        //          dokumentmetadata={temaListe[0].dokumentmetadata}
        //          valgt={this._erValgt("alle")}
        //          velgSak={this.props.velgSak}
        ///>
        //{temalisteelementer}

        return (
            <div>
                <ul className="sakstemaliste">
                    <li>Test</li>
                </ul>
            </div>
        );
    }
}


Sakstema.propTypes = {
    sakstema: PT.array.isRequired,
    velgSak: PT.func.isRequired
};

export default SakstemaListe;

