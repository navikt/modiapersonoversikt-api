import React, { PropTypes as PT } from 'react';

function Kontaktinformasjon({ kanal, mottakerInformasjon, resources }) {
    const kanalKontaktet = resources.get(`varsel.tilbakemelding.${kanal}`);

    const kontaktInformasjon = kanal !== 'NAV.NO' ? mottakerInformasjon : '';

    return (
        <span className="kontaktinformasjon">
            {kanalKontaktet}
            {kontaktInformasjon}
        </span>
    );
}

Kontaktinformasjon.propTypes = {
    kanal: PT.string.isRequired,
    mottakerInformasjon: PT.string.isRequired,
    resources: PT.object.isRequired
};

export default Kontaktinformasjon;
