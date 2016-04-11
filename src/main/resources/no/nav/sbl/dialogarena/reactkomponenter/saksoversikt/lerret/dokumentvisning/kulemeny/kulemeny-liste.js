import React from 'react';
import Kulemeny from './kulemeny';

const renderKulemeny = (kulemenypunkt, index) => (
    <Kulemeny
      dokref={kulemenypunkt.dokumentreferanse}
      tittel={kulemenypunkt.tittel}
      key={kulemenypunkt.dokumentreferanse}
      initialState={index === 0}
    />);

class KulemenyListe extends React.Component {
    componentDidMount() {
        const indexValgtDokument = this.props.indexValgtDokument;
        const list = document.getElementById('kulemenyliste');
        if (indexValgtDokument && indexValgtDokument >= 0 && list !== null) {
            setTimeout(() => list.querySelectorAll('li')[indexValgtDokument].querySelector('input').click(), 0);
        }
    }

    render() {
        const { dokumentmetadata } = this.props;
        if (dokumentmetadata.length < 2) {
            return <noscript/>;
        }

        const kulemenypunkter = dokumentmetadata.map(renderKulemeny);
        return <ul className="kulemeny" id="kulemenyliste">{kulemenypunkter}</ul>;
    }
}


KulemenyListe.propTypes = {
    dokumentmetadata: React.PropTypes.arrayOf(React.PropTypes.shape({
        dokumentreferanse: React.PropTypes.string.isRequired,
        tittel: React.PropTypes.string.isRequired
    })).isRequired,
    indexValgtDokument: React.PropTypes.number
};

export default KulemenyListe;
