import PT from 'prop-types';
import createProviderComponent from './../utils/context-creator';

export default createProviderComponent({
    miljovariabler: PT.object.isRequired
});
