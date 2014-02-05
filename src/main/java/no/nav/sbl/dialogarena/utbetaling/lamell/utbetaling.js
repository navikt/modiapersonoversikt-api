$(function () {
    $('.utbetaling-ramme').addKeyNavigation({itemsSelector: '.utbetalingslinje'});

    Modig.shortcutListener.on({keyCode: 13}, function () {
        var $activeElement = $(document.activeElement);
        if ($activeElement.parent('.utbetaling-ramme-innhold')) {
            Utbetalinger.toggleDetaljPanel($activeElement);
        }
    });
});

var Utbetalinger = (function () {
    var utbetalingslinjeSelector = '.utbetaling-ramme .utbetalingslinje';

    var haandterDetaljPanelVisning = function (detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        $detaljPanel.animate({height: 'toggle'}, 900);
        $detaljPanel.parent().toggleClass('ekspandert');
        $('html,body').animate({scrollTop: $($detaljPanel).parent().offset().top - 76}, 'slow');
        $detaljPanel.focus();
    };

    var toggleDetaljPanel = function ($element) {
        $element.children('.detaljpanel').animate({height: 'toggle'}, 200);
        $element.toggleClass('ekspandert');
    };

    var skrivUt = function ($element) {
        var $printCopy = $element.clone();
        $printCopy.children('.detaljpanel').css('display', 'block');
        kopierOgSkrivUt($printCopy.html());
    };

    function kopierOgSkrivUt(html) {
        $('body > .print .content').html(html);
        var date = new Date();
        var day = date.getDate();
        var month = date.getMonth() + 1;
        var year = date.getFullYear();
        var ddmmyyyy = ((day < 10 ? '0' : '') + day) + '.' + ((month < 10 ? '0' : '') + month) + '.' + year;
        $('body > .print .dato-utskrift #dato').text(ddmmyyyy);
        window.print();
    }

    return {
        haandterDetaljPanelVisning: haandterDetaljPanelVisning,
        toggleDetaljPanel: toggleDetaljPanel,
        skrivUt: skrivUt,
        utbetalingslinjeSelector: utbetalingslinjeSelector
    }
})();

// Event listeners
$(document).on('click', Utbetalinger.utbetalingslinjeSelector, function () {
    Utbetalinger.toggleDetaljPanel($(this));
});

$(document).on('click', Utbetalinger.utbetalingslinjeSelector + ' .skriv-ut', function (event) {
    var $utbetalingslinje = $(this).closest(Utbetalinger.utbetalingslinjeSelector);
    Utbetalinger.skrivUt($utbetalingslinje);
    event.stopPropagation();
});

