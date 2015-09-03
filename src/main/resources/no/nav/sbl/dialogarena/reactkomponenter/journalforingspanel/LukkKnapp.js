import React from 'react';

class LukkKnapp extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <button className="lukk-knapp" aria-label="lukk"></button>
        );
    }
}

export default LukkKnapp;

