import React from 'react';
import { FormattedMessage } from 'react-intl';

export const ettersendelseTil = (soknadTittel) =>
    <FormattedMessage id="ettersending.til.soknad" values={{ soknadTittel }}/>;
