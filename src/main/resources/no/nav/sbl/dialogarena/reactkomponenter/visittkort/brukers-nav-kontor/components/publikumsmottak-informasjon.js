import React from 'react';
import PT from 'prop-types';

import { publikumsmottakType } from '../types';

function AntallPublikumsmottakInformasjon({ publikumsmottak }) {
    if (publikumsmottak.length > 1) {
        return <p className="infoblokk">Det finnes flere publikumsmottak....</p>;
    }
    return null;
}
AntallPublikumsmottakInformasjon.propTypes = {
    publikumsmottak: PT.arrayOf(publikumsmottakType).isRequired
};

export default AntallPublikumsmottakInformasjon;
