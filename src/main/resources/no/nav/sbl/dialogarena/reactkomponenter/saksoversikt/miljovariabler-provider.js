import { PropTypes as PT } from 'react';
import createProviderComponent from './../utils/context-creator';

export default createProviderComponent({
    miljovariabler: PT.object.isRequired
});
