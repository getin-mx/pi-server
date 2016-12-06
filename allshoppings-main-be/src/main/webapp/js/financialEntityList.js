$(document).ready(function() {

	$("input").uniform();

	$.extend( dataTableDefaults, {
		"sAjaxSource": "getFinancialEntityRecords",
		"aoColumns" : [
		               {"sWidth" : "75%"},
		               {"sWidth" : "10%"},
		               {"sWidth" : "10%"},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	$('#financialEntityRecords').dataTable(dataTableDefaults);
});
