$(document).ready(function() {

	var $body = $('body'),
	$content = $('#content');

	$("input").uniform();

	$.extend( dataTableDefaults, {
		"sAjaxSource": "getServiceRecords",
		"aoColumns" : [
		               {"sWidth" : "95%"},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	$('#serviceRecords').dataTable(dataTableDefaults);
});
