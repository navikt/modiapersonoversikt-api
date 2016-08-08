/* eslint no-script-url:0 */
import React from 'react';

class TilbakeKnapp extends React.Component {
    componentDidMount() {
        React.findDOMNode(this.refs.knapp).focus();
    }

    render() {
        const tilbake = this.props.tilbake;
        return (
            <a
                href="javascript:void(0)"
                className="tilbake-knapp"
                aria-label="tilbake"
                role="button"
                onClick={tilbake}
                ref="knapp"
            >Tilbake</a>
        );
    }
}

TilbakeKnapp.propTypes = {
    tilbake: React.PropTypes.func.isRequired
};

export default TilbakeKnapp;
