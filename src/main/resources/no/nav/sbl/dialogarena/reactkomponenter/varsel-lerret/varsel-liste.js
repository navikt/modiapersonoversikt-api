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

        return (
            <div className="varsel-liste">
                <div className="varsel-liste-header">
                    <span className="header-dato">Dato</span>
                    <span className="vekk"> | </span>
                    <span className="header-type">Type</span>
                    <span className="vekk"> | </span>
                    <span className="header-kanal">Sendt i kanal</span>
                </div>
                <ul className="reset-ul-styling">
                    {varselRad}
                </ul>
            </div>
        );
    }
}

export default VarselListe;