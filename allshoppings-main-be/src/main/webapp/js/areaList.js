$(document).ready(function() {

	var $body = $('body'),
	$content = $('#content');

	$("input").uniform();

	$.extend( dataTableDefaults, {
		"sAjaxSource": "getAreaRecords",
		"aoColumns" : [
		               {"sWidth" : "95%"},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	$('#areaRecords').dataTable(dataTableDefaults);
});
