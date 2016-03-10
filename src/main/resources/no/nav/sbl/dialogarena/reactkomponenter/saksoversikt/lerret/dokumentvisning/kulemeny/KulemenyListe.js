import React from 'react';
import Kulemeny from './Kulemeny';

const renderKulemeny = (kulemenypunkt, index) =>
    <Kulemeny
        dokref={kulemenypunkt.dokumentreferanse}
        tittel={kulemenypunkt.tittel}
        key={kulemenypunkt.dokumentreferanse}
        initalState={index === 0}
    />;

const KulemenyListe =
    ({ dokumentmetadata }) => {
        if (dokumentmetadata.length < 2) {
            return <noscript/>;
        }

        console.log('dokumentmetadata', dokumentmetadata);

        const kulemenypunkter = dokumentmetadata.map(renderKulemeny);
        return <ul className="kulemeny">{kulemenypunkter}</ul>;
    };

KulemenyListe.propTypes = {
    dokumentmetadata: React.PropTypes.arrayOf(React.PropTypes.shape({
        dokumentreferanse: React.PropTypes.string.isRequired,
        tittel: React.PropTypes.string.isRequired
    })).isRequired
};

export default KulemenyListe;
