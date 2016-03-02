import React, { PropTypes, Children, Component } from 'react';

function createProviderComponent(contextTypes) {

    class Provider extends Component {
        getChildContext() {
            const {children, ...props} = this.props;
            return props;
        }

        render() {
            let { children } = this.props;
            return Children.only(children)
        }
    }

    Provider.childContextTypes = contextTypes;

    if (contextTypes.hasOwnProperty('children')) {
        Provider.propTypes = contextTypes;
    } else {
        Provider.propTypes = {...contextTypes, children: PropTypes.element.isRequired};
    }

    return Provider;
}

export default createProviderComponent;
