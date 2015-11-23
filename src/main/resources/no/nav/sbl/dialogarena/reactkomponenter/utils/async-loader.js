import React from 'react';
import { isArray, includes, toArray } from 'lodash';
import Snurrepipp from './snurrepipp';
import AdvarselBoks from './../utils/advarsel-boks';
import Q from 'q';

function ensureArray(arr) {
    return isArray(arr) ? arr : [arr];
}

function combinedStates(states) {
    if (includes(states, 'rejected')) {
        return 'rejected';
    } else if (includes(states, 'pending')) {
        return 'pending';
    }

    return 'resolved';
}

class AsyncLoader extends React.Component {
    constructor(props) {
        super(props);
        const promiseStates = ensureArray(this.props.promises || []).map((p) => p.inspect().state);
        this.state = {
            status: combinedStates(promiseStates),
            data: null
        };
    }

    componentDidMount() {
        Q.all(ensureArray(this.props.promises)).then(function allResolved() { // fat-arrow kan ikke brukes hvis man vi ta ibruk `arguments`
            let dataargs;
            const args = toArray(arguments);
            let newdataargs;

            if (ensureArray(this.props.promises).length === 1) {
                dataargs = args[0][0];

                newdataargs = Object.keys(dataargs).reduce((acc, key)=> {
                    acc[key] = dataargs[key].value;
                    return acc;
                }, {});
            } else {
                dataargs = args.map((resp) => resp[0]);
            }

            this.setState({
                data: newdataargs,
                status: 'ok'
            });
        }.bind(this), function rejectHandler() {
            this.setState({
                status: 'rejected'
            });
        }.bind(this));// Binding må til siden vi ikke bruker fat-arrow
    }

    render() {
        let children;
        if (this.state.status === 'rejected') {
            children = <AdvarselBoks tekst="Henting av data mislyktes"/>;
        } else if (this.state.status === 'pending') {
            children = <Snurrepipp {...this.props.snurrepipp}/>;
        } else {
            const passingProps = {};
            passingProps[this.props.toProp] = this.state.data;

            const reactChildren = isArray(this.props.children) ? this.props.children.filter((child) => child !== null) : this.props.children;

            children = React.Children.map(reactChildren, function passPropsToChildren(elem) {
                return React.cloneElement(elem, passingProps);
            });
        }
        return (
            <div className="async-loader">
                {children}
            </div>
        );
    }
}

AsyncLoader.propTypes = {
    snurrepipp: React.PropTypes.object,
    children: React.PropTypes.element,
    promises: React.PropTypes.oneOfType([
        React.PropTypes.object,
        React.PropTypes.arrayOf(React.PropTypes.object)
    ]).isRequired,
    toProp: React.PropTypes.string
};
AsyncLoader.defaultProps = {
    toProp: 'data'
};

export default AsyncLoader;
