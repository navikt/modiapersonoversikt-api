import React, { PropTypes as PT } from 'react';
import Sakstema from './Sakstema';
import { finnNokkelinfoForSakstema } from './../../utils/siste-oppdatering'
import { FormattedMessage } from 'react-intl';

class SakstemaListe extends React.Component {
    _lagAlleTema(temaliste) {
        return [{
            temanavn: <FormattedMessage id="sakslamell.alletemaer"/>,
            temakode: 'alle',
            behandlingskjeder: temaliste[0].behandlingskjeder,
            dokumentMetadata: temaliste[0].dokumentMetadata
        }];
    }

    render() {
        const temaListe = this.props.sakstema;

        const temalisteelementer = this._lagAlleTema(temaListe)
            .concat(temaListe)
            .map((tema) => (
                <Sakstema tema={tema} velgSak={this.props.velgSak}
                          nokkelinfo={finnNokkelinfoForSakstema(tema.behandlingskjeder, tema.dokumentMetadata, 28)}
                          valgtTema={this.props.valgtTema}/>
            ));

        return (
            <div className="sakstemaliste">
                {temalisteelementer}
            </div>
        );
    }
}


Sakstema.propTypes = {
    sakstema: PT.array.isRequired,
    velgSak: PT.func.isRequired
};

export default SakstemaListe;
