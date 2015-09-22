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
        const varselRad = this.props.varsler.map(TIL_VARSEL_RAD);
        return (
            <div className="varsel-liste">
                <div className="varsel-liste-header">
                    <span className="header-dato">Dato</span>
                    <span className="header-type">Type</span>
                    <span className="header-kanal">Sendt i kanal</span>
                </div>

                {varselRad}
            </div>
        );
    }
}

export default VarselListe;