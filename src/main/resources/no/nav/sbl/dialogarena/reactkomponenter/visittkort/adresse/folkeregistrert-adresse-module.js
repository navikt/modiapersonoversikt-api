import React from 'react';
import PT from 'prop-types';

import Adresse from './components/adresse';
import Tilleggsadresse from './components/tilleggsadresse';

class FolkeregistrertAdresse extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <tr>
                <th className="overskrift">Folkeregistrert adresse</th>
                <td><Tilleggsadresse adresse={this.props.tilleggsadresse} /></td>
                <td><Adresse {...this.props} /></td>
            </tr>
        );
    }
}

FolkeregistrertAdresse.propTypes = {
    adresseType: PT.string.isRequired,
    adresse: PT.object,
    tilleggsadresse: PT.string
};

export default FolkeregistrertAdresse;
