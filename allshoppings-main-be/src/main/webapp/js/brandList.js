$(document).ready(function() {
						   
		$("input").uniform();
		
		$.extend( dataTableDefaults, {
			"sAjaxSource": "getBrandRecords",
			"aoColumns" : [
			               {"sWidth" : "30px", "bSortable": false},
			               {"sWidth" : "75%"},
			               {"sWidth" : "10%"},
			               {"sWidth" : "10%"},
			               {"sWidth" : "5%", "bSortable": false},
			               ]
		});
		$('#brandRecords').dataTable(dataTableDefaults);
});
