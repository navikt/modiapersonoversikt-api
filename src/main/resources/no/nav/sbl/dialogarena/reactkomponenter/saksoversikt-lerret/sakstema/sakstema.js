import React from 'react';
import DokumentListe from './dokumentliste/dokumentliste';

class Sakstema extends React.Component {
    render() {
        return ( <DokumentListe dokumentMetadata={this.props.dokumentMetadata}/>);
    }
}
Sakstema.propTypes = {
    dokumentMetadata: React.PropTypes.array.isRequired
};

export default Sakstema;

