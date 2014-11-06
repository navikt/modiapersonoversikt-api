$(document).on('click', function () {
    $('.temagruppe-liste').hide();
});

$(document).on('click', '.temagruppe-liste', function (e) {
    e.stopPropagation();
});

$(document).on('click', '.velg-temagruppe', function (e) {
    $('.temagruppe-liste')
        .toggle()
        .find('input[type=radio]').first().blur().focus();
    e.stopPropagation();
});

$(document).on('click', '.plukk-knapp', function (e) {
    $('.velg-temagruppe').addClass('plukket');
    var valgTemagruppe = $('.oppgavepanel .temagruppe-liste').find(':checked');

    if (valgTemagruppe.length == 0) {
        var radiogroup =$('.temagruppe-liste').show()
            .find('[role=radiogroup]');

        radiogroup
            .attr('aria-invalid', true)
            .attr('aria-describedBy', 'aria-error-' + radiogroup.attr('id'));

    } else {
        $('.temagruppe-liste')
            .find('[role=radiogroup]')
            .removeAttr('aria-invalid')
            .removeAttr('aria-describedBy');
    }
    e.stopPropagation();
});
window.fokusPlukkOppgaveTemagruppe = function(){
    $('.temagruppe-liste').show()
        .find('[role=radiogroup]').blur().focus();
}
$(document).on('keydown', '.plukk-knapp, .velg-temagruppe', function (e) {
    var $target = $(e.currentTarget);
    if (e.keyCode == 13 || e.keyCode == 32) {
        $target.click();
    }
});