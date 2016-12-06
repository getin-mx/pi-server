$(document).ready(function() {

	var $body = $('body'),
	$content = $('#content');

	$("input").uniform();

	$.extend( dataTableDefaults, {
		"sAjaxSource": "getOfferTypeRecords",
		"aoColumns" : [
		               {"sWidth" : "95%"},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	$('#offerTypeRecords').dataTable(dataTableDefaults);
});
