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

    render() {
        console.log('this.props.sakstema', this.props.sakstema);

        const temaListe = this.props.sakstema.sort(sakstemaComparator);

        console.log('SakstemaListe this.props', this.props);


        const temalisteelementer = temaListe.map((tema) => (
            <Sakstema tema={tema} velgSak={this.props.velgSak} valgtTema={this.props.valgtTema}/>
        ));

        console.log('temalistelementer', temalisteelementer);


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
                    {temalisteelementer}
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

