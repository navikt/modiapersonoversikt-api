import { Children, Component } from 'react';
import { omit } from './utils-module';
import PT from 'prop-types';

function createProviderComponent(contextTypes) {
    class Provider extends Component {
        getChildContext() {
            return omit(this.props, 'children');
        }

        render() {
            const { children } = this.props;
            return Children.only(children);
        }
    }

    Provider.childContextTypes = contextTypes;

    if (contextTypes.hasOwnProperty('children')) {
        Provider.propTypes = contextTypes;
    } else {
        Provider.propTypes = { ...contextTypes, children: PT.element.isRequired };
    }

    return Provider;
}

export default createProviderComponent;
