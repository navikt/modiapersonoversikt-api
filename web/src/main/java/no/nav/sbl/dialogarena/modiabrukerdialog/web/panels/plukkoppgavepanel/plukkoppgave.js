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