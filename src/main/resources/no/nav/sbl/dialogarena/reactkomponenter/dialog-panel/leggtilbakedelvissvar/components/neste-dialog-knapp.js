import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import PT from 'prop-types';

class NesteDialogKnapp extends Component {

    componentDidMount() {
        ReactDOM.findDOMNode(this.refs.nesteknapp).focus();
    }

    render() {
        return (
            <button ref="nesteknapp" className={"knapp-hoved startNesteDialog"} onClick={this.props.startNesteDialogCallback}>
                Gå til neste spørsmål
            </button>
        );
    }
}

NesteDialogKnapp.prototype = {
    startNesteDialogCallback: PT.func.isRequired
}

export default NesteDialogKnapp;