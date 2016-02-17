import React from 'react';

class Sakstema extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { tema } = this.props;
        console.log(this.props);
        return (
            <a> {tema.id} </a>
        );
    }

}

Sakstema.PropTypes = {
    tema: React.PropTypes.string.isRequired
};

export default Sakstema;