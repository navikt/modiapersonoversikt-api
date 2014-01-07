var utbetalingslinjeSelector = '.utbetaling-ramme .utbetalingslinje';

$(document).on('click', utbetalingslinjeSelector, function () {
    $(this).children('.detaljpanel').animate({height: 'toggle'}, 300);
    $(this).find('.ekspander-pil').toggleClass('ekspandert');
});

$(document).on('click', utbetalingslinjeSelector + ' .skriv-ut', function(event) {
    var $utbetalingslinje = $(this).closest(utbetalingslinjeSelector);
    Utbetalinger.skrivUt($utbetalingslinje);
    event.stopPropagation();
});


var Utbetalinger = (function () {

    var haandterDetaljPanelVisning = function (detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        $detaljPanel.animate({height: 'toggle'}, 900);
        $detaljPanel.siblings('.main').find('.ekspander-pil').toggleClass('ekspandert');
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