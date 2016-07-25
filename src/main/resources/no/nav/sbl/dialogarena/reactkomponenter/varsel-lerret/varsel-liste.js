import React, { PropTypes as PT } from 'react';
import VarselRad from './varsel-rad';

class VarselListe extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { style, store, varsler } = this.props;
        const varselRad = varsler.map((varsel) => <VarselRad varsel={varsel} store={store}/>);
        const resources = store.getResources();

        return (
            <div className="varsel-liste" style={style}>
                <ul className="reset-ul-styling">
                    <li className="varsel-liste-header">
                        <span className="header-dato">
                            {resources.getOrElse('varsel.ledetekst.header.dato', 'Dato')}
                        </span>
                        <span className="vekk"> | </span>
                        <span className="header-type">
                            {resources.getOrElse('varsel.ledetekst.header.type', 'Type')}
                        </span>
                        <span className="vekk"> | </span>
                        <span className="header-kanal">
                            {resources.getOrElse('varsel.ledetekst.header.kanal', 'Sendt i kanal')}
                        </span>
                    </li>
                    {varselRad}
                </ul>
            </div>
        );
    }
}

VarselListe.propTypes = {
    style: PT.object,
    store: PT.object.isRequired,
    varsler: PT.array
};

export default VarselListe;
