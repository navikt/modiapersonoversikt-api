import React, { PropTypes as PT } from 'react';
import { FormattedDate } from 'react-intl';
import { dokumentMetadataTilJSDate } from './../../utils/dato-utils';

class Sakstema extends React.Component {

    _onClick(tema) {
        return () => {
            this.props.onClickSakstema(tema);
        };
    }

    render() {
        const tema = this.props.tema;
        const temakode = this.props.temakode;
        const dokumentmetadata = this.props.dokumentmetadata;
        const valgt = this.props.valgt ? 'valgt' : '';
        const datostreng = hentDatostreng(dokumentmetadata);

        return (
            <li>
                <a className={'saksoversikt-liste-element' + " " + valgt} href="#"
                   onClick={this._onClick(temakode).bind(this)}>
                    <label className="temaliste-label datotekst">{datostreng}</label>
                    <label className="temaliste-label stortekst">{tema}</label>
                </a>
            </li>
        );
    }
}

function hentDatostreng(dokumentmetadata) {
    //Hack enn så lenge. Bør bruke FormattedDate.
    if (dokumentmetadata.length === 0) {
        return "";
    } else {
        const dato = dokumentmetadata[0].dato;
        const day = dato.dayOfMonth;
        const year = dato.year;
        const month = dato.monthValue;
        return day + "." + month + "." + year;
    }
};

Sakstema.propTypes = {
    tema: PT.object.isRequired,
    velgSak: PT.func.isRequired
};

export default Sakstema;
