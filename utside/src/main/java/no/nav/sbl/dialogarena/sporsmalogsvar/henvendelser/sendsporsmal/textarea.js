var MAKS_TEGN = 1000;
var NAERMER_SEG_LIMIT = 800;
var informertPaste = false;
var informertKeyup = false;

$(document).ready(function() {
    var $tekst = $("#tekst");
    $tekst.bind('input propertychange', function () {
        tilpassHoyde(this);
        tellAntallTegn(this);
    });
    $tekst.bind('paste', function() {
        var tekst = this;
        // Må sette 0-timemout for å telle antall tegn etter at de er limt inn
        setTimeout(function() {
            informerHvisOverskriderMax(tekst);
        }, 0);
    });
    $tekst.keyup(function() {
        var tekst = this;
        setTimeout(function () {
            informerHvisNaermerSegMax(tekst);
        }, 0);
    });
    $tekst.focus();
    $tekst.select();

    function tilpassHoyde(textArea) {
        textArea.style.height = 'auto';
        textArea.style.height = textArea.scrollHeight + 'px';
    }
    function tellAntallTegn(textArea) {
        var antall = textArea.value.length;
        var $antall = $("#antall");
        $antall.text(antall + " av " + MAKS_TEGN + " tegn");
        var overskrider = "overskrider";
        if (antall > MAKS_TEGN) {
            $antall.addClass(overskrider);
        } else {
            $antall.removeClass(overskrider)
        }
    }

    function informerHvisOverskriderMax(textArea) {
        var antall = textArea.value.length;
        if (antall > MAKS_TEGN && !informertPaste) {
            informertPaste = true;
            window.alert("Teksten du limte inn er for lang.");
        }
    }

    function informerHvisNaermerSegMax(textArea) {
        var antall = textArea.value.length;
        if (antall == NAERMER_SEG_LIMIT && !informertKeyup) {
            informertKeyup = true;
            window.alert("Du har skrevet " + NAERMER_SEG_LIMIT + " tegn. Det betyr at du har " + (MAKS_TEGN - NAERMER_SEG_LIMIT)
                + " tegn igjen før maks antall tegn på " + MAKS_TEGN + " nås.")
        }
    }
});