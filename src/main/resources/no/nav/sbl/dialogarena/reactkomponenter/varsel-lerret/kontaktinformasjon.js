import React from 'react';

const Kontaktinformasjon = ({ kanal, mottakerInformasjon, resources }) => {
    const kanalKontaktet = resources.get(`varsel.tilbakemelding.${kanal}`);

    const kontaktInformasjon = kanal !== 'NAV.NO' ? mottakerInformasjon : '';

    return (
      <span className="kontaktinformasjon">
          {kanalKontaktet}
          {kontaktInformasjon}
      </span>
    );
};

export default Kontaktinformasjon;
