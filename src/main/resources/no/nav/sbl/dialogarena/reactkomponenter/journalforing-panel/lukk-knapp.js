import React from 'react';
import PT from 'prop-types';
import wicketSender from './../react-wicket-mixin/wicket-sender';

class LukkKnapp extends React.Component {
    constructor(props) {
        super(props);
        this.lukk = this.lukk.bind(this);
    }

    lukk(event) {
        event.preventDefault();
        wicketSender(this.props.wicketurl, this.props.wicketcomponent, 'lukkPanel');
    }

    render() {
        return (
            <button className="lukk-knapp" aria-label="lukk" onClick={this.lukk}></button>
        );
    }
}

LukkKnapp.propTypes = {
    wicketurl: PT.string.isRequired,
    wicketcomponent: PT.string.isRequired
};

export default LukkKnapp;
