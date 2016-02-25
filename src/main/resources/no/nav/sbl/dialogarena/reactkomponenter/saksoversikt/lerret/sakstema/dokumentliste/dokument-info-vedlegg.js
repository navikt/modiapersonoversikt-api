import React from 'react';

class DokumentinfoVedlegg extends React.Component {
    render() {
        const vedlegg = this.props.vedlegg;
        if (!vedlegg || vedlegg.length === 0) {
            return <div></div>;
        }

        const vedleggListe = vedlegg.map(dokumentVedlegg => (<li ><a className="vedleggtext">{dokumentVedlegg.tittel}</a></li>));

        return (
            <div>
                <p>Med følgende vedlegg</p>
                <ul className="vedleggliste">{vedleggListe}</ul>
            </div>
        );
    }
}

DokumentinfoVedlegg.propTypes = {
    vedlegg: React.PropTypes.array.isRequired
};

export default DokumentinfoVedlegg;
