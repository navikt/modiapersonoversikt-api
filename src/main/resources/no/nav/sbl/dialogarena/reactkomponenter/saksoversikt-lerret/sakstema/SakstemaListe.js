import Sakstema from './Sakstema';
import React from 'react';


class SakstemaListe extends React.Component {


    render() {
        const temaListe = this.props.sakstema.sort(function (a, b) {
            return !a.sistOppdatertDato ? 1 : new Date(b.sistOppdatertDato) - new Date(a.sistOppdatertDato);
        });

        const temalisteelementer = temaListe.map(tema => <Sakstema tema={tema.temanavn} temakode={tema.temakode}
                                                                   dokumentmetadata={tema.dokumentmetadata}
                                                                   valgt={this.props.erValgt(tema.temakode)}
                                                                   onClickSakstema={this.props.velgSak}/>);

        const alleSakstemaElement = temalisteelementer.length > 1 ?
            <Sakstema tema={this.props.tekster["sakslamell.alletemaer"]} temakode={"alle"} dokumentmetadata={temaListe[0].dokumentmetadata}
                      valgt={this.props.erValgt("alle")}
                      onClickSakstema={this.props.velgSak}/> : <div />;
        return (
            <div>
                <ul className="sakstemaliste">
                    {alleSakstemaElement}
                    {temalisteelementer}
                </ul>
            </div>
        );
    }
}


Sakstema.propTypes = {
    tema: React.PropTypes.string.isRequired,
    temakode: React.PropTypes.string.isRequired,
    dokumentmetadata: React.PropTypes.shape({
        dato: React.PropTypes.shape({
            month: React.PropTypes.string.isRequired,
            dayOfMonth: React.PropTypes.string.isRequired,
            year: React.PropTypes.string.isRequired
        }).isRequired
    }).isRequired,
    dato: React.PropTypes.shape({
        month: React.PropTypes.string.isRequired,
        dayOfMonth: React.PropTypes.string.isRequired,
        year: React.PropTypes.string.isRequired
    }).isRequired
};

export default SakstemaListe;

