$(function() {
    $('#utvidetPersonsokForm').navAjaxLoader({
        event       : 'submit',
        placeElement: $('#personsokResult'),
        placement   : 'before',
        loadImage   : 'img/ajaxloader/svart/loader_svart_96.gif',
        css         : 'margin-bottom: 20px;'
    });

});

