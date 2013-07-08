$(function() {
    $('#hiddenSubmit').navAjaxLoader({
        event       : 'click',
        placeElement: '#personsokResult',
        placement   : 'before',
        loadImage   : '../img/ajaxloader/svart/loader_svart_96.gif',
        css         : 'margin-bottom: 20px;'
    });

    $('.search-intern input[name="foedselsnummerInput"]').navAjaxLoader({
        event       : 'change',
        placeElement: '.lamell',
        placement   : 'before',
        loadImage   : '../img/ajaxloader/hvit/loader_hvit_96.gif',
        css         : 'margin: 30px 0 0 30px;'
    });

});

