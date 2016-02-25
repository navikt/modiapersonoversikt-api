import React from 'react';

class DokumentAvsender extends React.Component {
    render() {
        const {retning, avsender , navn} = this.props;

        const thisAvsender = retning === 'INN' ? <span className="avsenderbrukertext">{navn}</span> :
            <span className="avsendernavtext">{avsender}</span>

        return <div className="avsendertext"><span>Fra </span>{thisAvsender}</div>;
    }
}

DokumentAvsender.propTypes = {
    avsender: React.PropTypes.string.isRequired,
    retning: React.PropTypes.string.isRequired,
    navn: React.PropTypes.string
};

export default DokumentAvsender;