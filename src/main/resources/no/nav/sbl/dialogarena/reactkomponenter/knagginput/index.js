var React = ModiaJS.React;

var KnaggInput = React.createClass({
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
            nyeKnagger.pop();
            this.props.onChange(this.props.fritekst, nyeKnagger);
        }
    },
    onKeyDownProxy: function (event) {
        this.setState({
            selectionStart: this.refs.search.getDOMNode().selectionStart,
            selectionEnd: this.refs.search.getDOMNode().selectionEnd
        });
        this.props.onKeyDown(event);
    },
    onChangeProxy: function (event) {
        var data = finnKnaggerOgFritekst(event.target.value, this.props.knagger);
        this.props.onChange(data.fritekst, data.knagger);
    },
    fjernKnagg: function (index) {
        var nyeKnagger = this.props.knagger.slice(0);
        nyeKnagger.splice(index, 1);

        this.props.onChange(this.props.fritekst, nyeKnagger);
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
        var knagger = this.props.knagger.map(function (knagg, index) {
            var fjernKnagg = function (callback) {
                return function () {
                    callback(index);
                }
            };

            return (
                <span className="knagg">
                    {knagg}
                    <button aria-label={'Fjern knagg: ' + knagg} onClick={fjernKnagg(this.fjernKnagg)}>X</button>
                </span>
            );
        }.bind(this));

        return (
            <div className="knagg-input">
                <div className={"knagger clearfloat" + (this.state.focus ? " focus" : "")}>
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

//Trenger begge. Første for å starte komponenten fra ReactComponetPanel.java, og andre for å bruke require
window.ModiaJS.Components.KnaggInput = KnaggInput;
module.exports = KnaggInput;