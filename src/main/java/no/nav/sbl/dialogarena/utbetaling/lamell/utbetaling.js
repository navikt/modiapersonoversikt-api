var utbetalingslinjeSelector = '.utbetaling-ramme .utbetalingslinje';

$(document).on('click', utbetalingslinjeSelector, function (e) {
    if ($(e.target).is('a')) {
        e.preventDefault();
    } else {
        $(this).children('.detaljpanel').animate({height: 'toggle'}, 300);
    }
});

$(document).on('click', utbetalingslinjeSelector + ' .skriv-ut', function() {
    var $utbetalingslinje = $(this).closest(utbetalingslinjeSelector);
    Utbetalinger.skrivUt($utbetalingslinje);
    event.preventDefault();
});


var Utbetalinger = (function () {

    var haandterDetaljPanelVisning = function (detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        $detaljPanel.animate({height: 'toggle'}, 900);
        $('html,body').animate({scrollTop: $($detaljPanel).parent().offset().top - 76}, 'slow');
        $detaljPanel.focus();
    };

    var skrivUt = function ($element) {
        var $printCopy = $element.clone();
        $printCopy.children('.detaljpanel').css('display', 'block');
        kopierOgSkrivUt($printCopy.html());
    };

    function kopierOgSkrivUt(html) {
        $('body > .print .content').html(html);
        window.print();
    }

    return {
        haandterDetaljPanelVisning: haandterDetaljPanelVisning,
        skrivUt: skrivUt
    }
})();