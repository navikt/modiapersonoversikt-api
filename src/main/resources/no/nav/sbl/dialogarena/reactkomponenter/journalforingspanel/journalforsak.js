import React from 'react';

class JournalforSak extends React.Component {
    render() {
        var sak = this.props.sak;
        return <h1>{sak.saksIdVisning}</h1>;
    }
}

export default JournalforSak;

