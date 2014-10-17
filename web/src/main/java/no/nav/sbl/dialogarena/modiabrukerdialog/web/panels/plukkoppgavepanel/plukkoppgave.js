$(document).on('click', function () {
    $('.temagruppe-liste').hide();
});

$(document).on('click', '.temagruppe-liste', function (e) {
    e.stopPropagation();
});

$(document).on('click', '.velg-temagruppe', function (e) {
    $('.temagruppe-liste').toggle();
    e.stopPropagation();
});

$(document).on('click', '.plukk-knapp', function() {
    $('.velg-temagruppe').addClass('plukket');
});

$(document).on('keydown', '.plukk-knapp, .velg-temagruppe', function(e){
    var $target = $(e.currentTarget);
    if (e.keyCode == 13) {
        $target.click();
    }

    if ($target.is('.velg-temagruppe')) {
        $target.siblings('.temagruppe-liste').find('input[type=radio]').first().focus();
    }
});