//import React from 'react';
var React = require('react');

class TilbakeKnapp extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        var tilbake = this.props.tilbake;
        return (
            <button className="tilbake-knapp knapp-liten" aria-label="tilbake" onClick={tilbake}>Tilbake</button>
        );
    }
}

//export default TilbakeKnapp;
module.export='TilbakeKnapp';