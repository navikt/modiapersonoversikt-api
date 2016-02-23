import React from 'react';

function Tema({ tema, fnr }) {
    const link = `/modiabrukerdialog/person/${fnr}?temakode=${tema.temakode}#!saksoversikt`;

    return (
        <a className="subject" href={link}> {tema.temanavn} </a>
    );
}

Tema.PropTypes = {
    tema: React.PropTypes.string.isRequired
};

export default Tema;
