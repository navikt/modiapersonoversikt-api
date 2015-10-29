var React = require('react/addons');

var KnaggInput = React.createClass({
    getDefaultProps: function () {
        return {
            knagger: [],
            fritekst: '',
            tablisteId: null
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
        this.props.store.onKeyDown(document.getElementById(this.props.tablisteId), event);
    },
    onChangeProxy: function (event) {
        var data = finnKnaggerOgFritekst(event.target.value, this.props.knagger);
        this.props.store.onChange(data);
    },
    fjernKnagg: function (knagg, event) {
        event.preventDefault();

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
    componentDidUpdate: function () {
        IEHack.call(this);
    },
    render: function () {
        var knagger = this.props.knagger.map(function (knagg) {
            return (
                <span className="knagg">
                    {knagg}
                    <button aria-label={'Fjern knagg: ' + knagg} onClick={this.fjernKnagg.bind(this, knagg)}></button>
                </span>
            );
        }.bind(this));

        knagger = React.addons.createFragment({
            knagger: knagger
        });

        return (
            <div ref="knaggcontainer" className="knagg-input">
                <div className={"knagger" + (this.state.focus ? " focus" : "")}>
                    {knagger}
                    <input type="text" ref="search" className="search" placeholder={this.props.placeholder}
                           value={this.props.fritekst} title={this.props.placeholder}
                           onChange={this.onChangeProxy} onKeyDown={this.onKeyDownProxy} onKeyUp={this.handleKeyUp}
                           onFocus={this.focusHighlighting} onBlur={this.focusHighlighting}
                           aria-label={ariaLabel(this.props)} aria-controls={this.props['aria-controls']}/>
                    <img src="../img/sok.svg" alt="Forstørrelseglass-ikon" aria-hidden="true"/>
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

function IEHack() {
    //Dette er en IE hack.... Hvis man en gang i fremtiden ikke bruker IE 9, så kanskje man kan fjerne denne.
    var $knaggcontainer = $(this.refs.knaggcontainer.getDOMNode());
    if (!$knaggcontainer.is(':visible')) {
        return;
    }
    var $input = $knaggcontainer.find('input[type="text"]');
    var $knagger = $knaggcontainer.find('span.knagg');

    var maxWidth = $knaggcontainer.width();
    $knagger.each(function (index, knagg) {
        maxWidth -= $(knagg).outerWidth() + 4.2//knagg bredde + margin;
    });
    maxWidth -= 46;//Forstørrelseglass ikon

    $input.outerWidth(maxWidth);
    //End IE hack
}

module.exports = KnaggInput;