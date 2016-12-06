$(document).ready(function() {
	var $body = $('body'),
	$content = $('#content');

	$("input").uniform();
	$("input").bind("change", function () {
		changed = true;
	});

	$('a.fancybox').fancybox();
});

