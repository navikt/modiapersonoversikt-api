import React from 'react';

function Tema({ tema, sendToWicket }) {
    return (
        <a className="subject" href="javascript:void(0)"
           onClick={() => sendToWicket('ITEM_CLICK', tema.temakode)}>
            {tema.temanavn}
        </a>
    );
}

Tema.PropTypes = {
    tema: React.PropTypes.string.isRequired,
    sendToWicket: React.PropTypes.func.isRequired
};

export default Tema;
