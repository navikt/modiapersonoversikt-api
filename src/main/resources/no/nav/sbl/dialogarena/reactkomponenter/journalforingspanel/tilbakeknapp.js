import React from 'react';

class TilbakeKnapp extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        var tilbake = this.props.tilbake;
        return (
            <button className="tilbake-knapp" onClick={tilbake}></button>
        );
    }
}

export default TilbakeKnapp;