import React from 'react';
import VarselRad from './varsel-rad';

const TIL_VARSEL_RAD = (varsel) => {
    return <VarselRad varsel={varsel}/>;
};

class VarselListe extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const varselRad = this.props.varsler.map((varsel) => {
            return <VarselRad varsel={varsel} store={this.props.store}/>
        });
        const resources = this.props.store.getResources();

        return (
            <div className="varsel-liste">
                <ul className="reset-ul-styling">
                    <li className="varsel-liste-header">
                        <span className="header-dato">{resources.getOrElse('varsel.ledetekst.header.dato', 'Dato')}</span>
                        <span className="vekk"> | </span>
                        <span className="header-type">{resources.getOrElse('varsel.ledetekst.header.type', 'Type')}</span>
                        <span className="vekk"> | </span>
                        <span className="header-kanal">{resources.getOrElse('varsel.ledetekst.header.kanal', 'Sendt i kanal')}</span>
                    </li>
                    {varselRad}
                </ul>
            </div>
        );
    }
}

export default VarselListe;