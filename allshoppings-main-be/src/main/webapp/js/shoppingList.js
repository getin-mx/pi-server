$(document).ready(function() {
						   
	var $body = $('body'),
		$content = $('#content');
			
		$("input").uniform();
		
		$.extend( dataTableDefaults, {
			"sAjaxSource": "getShoppingRecords",
			"aoColumns" : [
			               {"sWidth" : "30px", "bSortable": false},
			               {"sWidth" : "45%"},
			               {"sWidth" : "20%"},
			               {"sWidth" : "20%"},
			               {"sWidth" : "10%"},
			               {"sWidth" : "5%", "bSortable": false},
			               ]
		});
		$('#shoppingRecords').dataTable(dataTableDefaults);
});
