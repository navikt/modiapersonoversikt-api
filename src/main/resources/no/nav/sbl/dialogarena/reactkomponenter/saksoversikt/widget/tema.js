import React from 'react';

function Tema({ tema, sendToWicket }) {
    const sendtowicket = () => sendToWicket('ITEM_CLICK', tema.temakode);
    return (
        <a tabIndex="-1" className="subject" href="javascript:void(0)" onClick={sendtowicket}>
            {tema.temanavn}
        </a>
    );
}

Tema.PropTypes = {
    tema: React.PropTypes.string.isRequired,
    sendToWicket: React.PropTypes.func.isRequired
};

export default Tema;
