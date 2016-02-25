import React from 'react';

class DokumentAvsender extends React.Component {
    render() {
        const {retning, avsender , navn} = this.props;

        if (retning === 'INN') {
            return <p>{'Fra ' + navn}</p>
        }
        else (retning === 'UT')
        {
            return <p>{'Fra ' + avsender}</p>

        }
    }
}

DokumentAvsender.propTypes = {
    avsender: React.PropTypes.string.isRequired,
    retning: React.PropTypes.string.isRequired,
    navn: React.PropTypes.string
};

export default DokumentAvsender;