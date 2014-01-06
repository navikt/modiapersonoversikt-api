var utbetalingslinjeSelector = '.utbetaling-ramme .utbetalingslinje';

$(document).on('click', utbetalingslinjeSelector, function (e) {
    if ($(e.target).is('a')) {
        e.preventDefault();
    } else {
        $(this).children('.detaljpanel').animate({height: 'toggle'}, 300);
    }
});

$(document).on('click', utbetalingslinjeSelector + ' .skriv-ut', function() {
    var utbetalingslinje = $(this).closest(utbetalingslinjeSelector).clone();
    utbetalingslinje.children('.detaljpanel').css('display', 'block');
    skrivUt(utbetalingslinje.html());
});

function skrivUt(html) {
    $('body > .print .content').html(html);
    window.print();
}

var Utbetalinger = (function() {
    var haandterDetaljPanelVisning = function(detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        $detaljPanel.animate({height: 'toggle'}, 900);
        $('html,body').animate({scrollTop: $($detaljPanel).parent().offset().top - 76}, 'slow');
        $detaljPanel.focus();
    };
    return {
        haandterDetaljPanelVisning : haandterDetaljPanelVisning
    }
})();