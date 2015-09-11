import React from 'react';

class TilbakeKnapp extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        var tilbake = this.props.tilbake;
        return (
            <a href="javascript:void(0)" className="tilbake-knapp" aria-label="tilbake" role="button" onClick={tilbake}>Tilbake</a>
        );
    }
}

export default TilbakeKnapp;