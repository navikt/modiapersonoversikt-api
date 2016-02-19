import React from 'react';

class Tema extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { tema, fnr } = this.props;
        const link = `/modiabrukerdialog/person/${fnr}?temakode=${tema.temakode}#!saksoversikt`;

        return (
            <a className="subject" href={link}> {tema.temanavn} </a>
        );
    }

}

Tema.PropTypes = {
    tema: React.PropTypes.string.isRequired
};

export default Tema;