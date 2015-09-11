import React from 'react';

class TilbakeKnapp extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        React.findDOMNode(this.refs.knapp).focus();
    }

    render() {
        var tilbake = this.props.tilbake;
        return (
            <a href="javascript:void(0)" className="tilbake-knapp" aria-label="tilbake" role="button" onClick={tilbake}
               ref="knapp">Tilbake</a>
        );
    }
}

export default TilbakeKnapp;