var React = ModiaJS.React;

var KnaggInput = React.createClass({
    getInitialState: function () {
        return {
            selectionStart: -1,
            selectionEnd: -1
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

        if (event.keyCode === 8 && selectionStart === 0 && selectionStart === selectionEnd) {//backspace
            if (this.props.knagger.length === 0) {
                return;
            }
            var nyeKnagger = this.props.knagger;
            nyeKnagger.pop();

            this.props.onChange(this.props.fritekst, nyeKnagger);
            event.preventDefault();
            event.stopPropagation();
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
    render: function () {
        var knagger = this.props.knagger.map(lagHTMLKnagger);

        var arialabel = this.props.value || this.props['aria-label'];

        return (
            <div className="knagg-input">
                <div className="knagger clearfloat">
                    {knagger}
                    <input type="text" ref="search" className="search" placeholder={this.props.placeholder} value={this.props.fritekst}
                        onChange={this.onChangeProxy} onKeyDown={this.onKeyDownProxy} onKeyUp={this.handleKeyUp}
                        aria-label={arialabel} aria-controls={this.props['aria-controls']} />
                </div>
            </div>
        );
    }
});
function finnKnaggerOgFritekst(fritekst, eksistendeKnagger) {
    while (fritekst.match(/^#(\S+)\s/)) {
        fritekst = fritekst.replace(/^#(\S+)\s/, function (fullmatch, capturegroup) {
            eksistendeKnagger.push(capturegroup);
            return "";
        });
    }

    return {
        knagger: eksistendeKnagger,
        fritekst: fritekst
    }
}

function lagHTMLKnagger(knagg) {
    return (
        <span className="knagg">
            {knagg}
            <button>X</button>
        </span>
    );
}


//Trenger begge. Første for å starte komponenten fra ReactComponetPanel.java, og andre for å bruke require
window.ModiaJS.Components.KnaggInput = KnaggInput;
module.exports = KnaggInput;