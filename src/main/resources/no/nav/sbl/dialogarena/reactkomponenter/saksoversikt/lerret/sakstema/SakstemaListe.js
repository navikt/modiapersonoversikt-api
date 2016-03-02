import React, { PropTypes as PT } from 'react';
import Sakstema from './Sakstema';
import { finnNokkelinfoForSakstema } from './../../utils/siste-oppdatering'
import { FormattedMessage } from 'react-intl';

const SakstemaListe = ({sakstema, valgtTema, velgSak}) => {
    const temaListe = sakstema.map((tema) => (
        <Sakstema key={tema.temakode} tema={tema} velgSak={velgSak}
                  nokkelinfo={finnNokkelinfoForSakstema(tema.behandlingskjeder, tema.dokumentMetadata, 411)}
                  valgtTema={valgtTema}/>
    ));

    return (
        <div className="sakstemaliste">
            {temaListe}
        </div>
    );
};

SakstemaListe.propTypes = {
    sakstema: PT.array.isRequired,
    velgSak: PT.func.isRequired
};

export default SakstemaListe;
