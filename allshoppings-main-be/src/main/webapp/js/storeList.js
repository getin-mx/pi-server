$(document).ready(function() {
						   
		$("input").uniform();
		
		$.extend( dataTableDefaults, {
			"sAjaxSource": "getStoreRecords",
			"aoColumns" : [
			               {"sWidth" : "25%"},
			               {"sWidth" : "25%"},
			               {"sWidth" : "25%"},
			               {"sWidth" : "5%"},
			               {"sWidth" : "5%"},
			               {"sWidth" : "10%"},
			               {"sWidth" : "5%", "bSortable": false},
			               ]
		});
		$('#storeRecords').dataTable(dataTableDefaults);
});
