var React = require('react');

var KnaggInput = React.createClass({
    getDefaultProps: function () {
        return {
            knagger: [],
            fritekst: ''
        };
    },
    getInitialState: function () {
        return {
            selectionStart: -1,
            selectionEnd: -1,
            focus: false
        }
    },
    componentDidMount: function () {
        if (this.props['auto-focus']) {
            this.refs.search.getDOMNode().focus();
        }
    },
    handleKeyUp: function (event) {
        var selectionStart = this.state.selectionStart;
        var selectionEnd = this.state.selectionEnd;

        if (event.keyCode === 8 /* backspace */ && selectionStart === 0 && selectionStart === selectionEnd) {
            if (this.props.knagger.length === 0) {
                return;
            }
            var nyeKnagger = this.props.knagger.slice(0);
            this.props.store.slettKnagg(nyeKnagger.pop());
        }
    },
    onKeyDownProxy: function (event) {
        this.setState({
            selectionStart: this.refs.search.getDOMNode().selectionStart,
            selectionEnd: this.refs.search.getDOMNode().selectionEnd
        });
        this.props.store.onKeyDown(this.props.tabliste, event);
    },
    onChangeProxy: function (event) {
        var data = finnKnaggerOgFritekst(event.target.value, this.props.knagger);
        this.props.store.onChange(data);
    },
    fjernKnagg: function (knagg) {
        this.props.store.slettKnagg(knagg);
        this.refs.search.getDOMNode().focus();
    },
    focusHighlighting: function (event) {
        if (event.type === 'focus') {
            this.setState({focus: true});
        } else {
            this.setState({focus: false});
        }
    },
    render: function () {
        var knagger = this.props.knagger.map(function (knagg) {
            return (
                <span className="knagg">
                    {knagg}
                    <button aria-label={'Fjern knagg: ' + knagg} onClick={this.fjernKnagg.bind(this, knagg)}>X</button>
                </span>
            );
        }.bind(this));

        knagger = React.addons.createFragment({
            knagger: knagger
        });

        return (
            <div className="knagg-input">
                <div className={"knagger" + (this.state.focus ? " focus" : "")}>
                    {knagger}
                    <input type="text" ref="search" className="search" placeholder={this.props.placeholder} value={this.props.fritekst}
                        onChange={this.onChangeProxy} onKeyDown={this.onKeyDownProxy} onKeyUp={this.handleKeyUp}
                        onFocus={this.focusHighlighting} onBlur={this.focusHighlighting}
                        aria-label={ariaLabel(this.props)} aria-controls={this.props['aria-controls']} />
                </div>
            </div>
        );
    }
});

function ariaLabel(props) {
    var knagger = props.knagger;
    var fritekst = props.fritekst;

    if (knagger.length === 0 && fritekst.length === 0) {
        return props['aria-label'];
    }

    var label = [];
    if (knagger.length > 0) {
        label.push("Knagger: " + knagger.join(" "));
    }
    if (fritekst.length > 0) {
        label.push("Fritekst: " + fritekst);
    }
    return label.join(" ");
}

function finnKnaggerOgFritekst(fritekst, eksistendeKnagger) {
    fritekst = fritekst.replace(/\B#(\S+)\s/g, function (fullmatch, capturegroup) {
        eksistendeKnagger.push(capturegroup);
        return "";
    });

    return {
        knagger: eksistendeKnagger,
        fritekst: fritekst
    }
}

module.exports = KnaggInput;