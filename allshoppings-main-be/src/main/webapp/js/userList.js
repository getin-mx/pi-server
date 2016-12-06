$(document).ready(function() {
						   
		$("input").uniform();
		
		$.extend( dataTableDefaults, {
			"sAjaxSource": "getUserRecords",
			"aoColumns" : [
			               {"sWidth" : "25%"},
			               {"sWidth" : "25%"},
			               {"sWidth" : "20%"},
			               {"sWidth" : "15%"},
			               {"sWidth" : "10%"},
			               {"sWidth" : "5%", "bSortable": false},
			               ]
		});
		$('#userRecords').dataTable(dataTableDefaults);
});
