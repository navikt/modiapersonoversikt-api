$(function() {
	setHeight();
	$(window).on('resize', function() {
		setHeight();
	});
	$(document).on('resize', '.flexbox.row [class*=flex]', function() {
		setHeight();
	});
});

function setHeight() {
	var $flexbox = $('.flexbox.row');
	var $childs  = $flexbox.find('[class*=flex]');
	$childs.css('height', 'auto');
	$flexbox.find('.flex1').css('width', '25%');
	$flexbox.find('.flex2').css('width', '50%');
	var height = $flexbox.height();
	$childs.css({ 'float': 'left', 'height': height });
	var $flik = $('.saksbehandlerpanelflik');
	var left = ($(window).width() - $flik.width()) / 2;
	$flik.css('marginLeft', left);
}
