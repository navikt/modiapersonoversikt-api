import React, { PropTypes as PT } from 'react';
import { FormattedDate } from 'react-intl';
import { dokumentMetadataTilJSDate } from './../../utils/dato-utils';

function formaterBehandlingsstatus(behandlingsstatus) {

    const statuser = behandlingsstatus.props.values;
    if(statuser) {
        const antallFerdige = statuser.antallSoknaderFerdigBehandlet.props.values.antall;
        const antallFerdigeTekst = antallFerdige > 1? antallFerdige + " søknader ferdig behandlet" : antallFerdige == 1? "Søknad ferdig behandlet": "" ;
        const antallUnderBehandling = statuser.antallSoknaderUnderBehandling.props.values.antall;
        const antallUnderbehandlingTekst = antallUnderBehandling > 1? antallUnderBehandling + " søknader ferdig behandlet" : antallUnderBehandling == 1? "Søknad ferdig behandlet": "" ;

        return antallUnderBehandling  >=  antallFerdige? antallUnderbehandlingTekst : antallFerdigeTekst;
    }
    return "";
}

class Sakstema extends React.Component {
    render() {
        const { tema, valgtTema, velgSak, nokkelinfo} = this.props;
        // Sjekk på temakode ettersom 'alletemaet' blir laget på nytt ved rerender.
        const erValgt = tema.temakode === valgtTema.temakode ? 'valgt' : '';
        const id = `sakstemaRadioListe--${tema.temakode}`;
        const sisteOppdatering = nokkelinfo.sisteOppdatering? nokkelinfo.sisteOppdatering : "";
        const statusTekst = nokkelinfo.behandlingsstatus? formaterBehandlingsstatus(nokkelinfo.behandlingsstatus) : "";

        return (
            <div className={`saksoversikt-liste-element ${erValgt}`}>
                <input type="radio" id={id} readOnly checked={erValgt} name="sakstemaRadioListe"
                       onClick={() => velgSak(tema)}
                />
                <label htmlFor={id}>
                    <p className="temaliste-label datotekst">{sisteOppdatering.toString()}</p>
                    <p className="temaliste-label stortekst">{tema.temanavn}</p>
                    <p className="temaliste-label datotekst">{statusTekst}</p>
                </label>
            </div>
        );
    }
}

Sakstema.propTypes = {
    tema: PT.object.isRequired,
    valgtTema: PT.object.isRequired,
    velgSak: PT.func.isRequired
};

export default Sakstema;
