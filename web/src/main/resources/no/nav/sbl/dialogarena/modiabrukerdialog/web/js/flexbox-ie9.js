$(function() {
	setFlexHeight();
	$(window).on('resize', function() {
		setFlexHeight();
	});
	$(document).on('resize', '.flexbox.row [class*=flex]', function() {
		setFlexHeight();
	});
});

function setFlexHeight() {
	var $flexbox = $('.flexbox.row');
	var $childs  = $flexbox.find('[class*=flex]');
	$childs.css('height', 'auto');
	$flexbox.find('.flex1').css({ 'display': 'table-cell', 'width': '25%' });
	$flexbox.find('.flex2').css({ 'display': 'table-cell', 'width': '50%', 'verticalAlign': 'top' });
}
