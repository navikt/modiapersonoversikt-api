const MAKS_TEGN = 1000;
function textarea() {
    var $tekst = $("#tekst");
    tellAntallTegn($tekst);
    $tekst.bind('input propertychange', function() {
        tilpassHoyde(this);
        tellAntallTegn($(this));
    });
}

function tilpassHoyde(textArea) {
    textArea.style.height = 'auto';
    textArea.style.height = textArea.scrollHeight + 'px';
}

function tellAntallTegn(textArea) {
    var antall = textArea.val().length;
    var $antall = $("#antall");
    $antall.text(antall + " av " + MAKS_TEGN + " tegn");
}