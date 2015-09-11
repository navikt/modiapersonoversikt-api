import React from 'react';
import WicketSender from './../reactwicketmixin/wicketsender.js';

class LukkKnapp extends React.Component {
    constructor(props) {
        super(props);
        this.lukk = this.lukk.bind(this);
    }

    lukk(event) {
        event.preventDefault();
        WicketSender(this.props.wicketurl, this.props.wicketcomponent, 'lukkPanel');
    }

    render() {
        return (
            <button className="lukk-knapp" aria-label="lukk" onClick={this.lukk}></button>
        );
    }
}

export default LukkKnapp;