import React, { PropTypes as PT } from 'react';
import Sakstema from './sakstema';
import { finnNokkelinfoForSakstema } from './../../utils/siste-oppdatering/siste-oppdatering';

const SakstemaListe = ({ sakstema, valgtTema, velgSak }, { miljovariabler }) => {
    const temaListe = sakstema.map((tema) => (
        <Sakstema
            key={tema.temakode}
            tema={tema}
            velgSak={velgSak}
            nokkelinfo={finnNokkelinfoForSakstema(tema.behandlingskjeder,
            tema.dokumentMetadata,
            miljovariabler['behandlingsstatus.synlig.antallDager'])}
            valgtTema={valgtTema}
        />
    ));

    return (
        <div className="sakstemaliste">
            {temaListe}
        </div>
    );
};

SakstemaListe.propTypes = {
    sakstema: PT.array.isRequired,
    valgtTema: PT.object,
    velgSak: PT.func.isRequired
};

SakstemaListe.contextTypes = {
    miljovariabler: PT.object.isRequired
};

export default SakstemaListe;
