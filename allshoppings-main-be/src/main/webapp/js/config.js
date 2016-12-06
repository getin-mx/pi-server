/*----------------------------------------------------------------------*/
/* Set some Standards
/* This file not required! It just demonstrate how you can define
/* standards in one configuration file
/*----------------------------------------------------------------------*/

var config = {
	tooltip :{
		gravity: 'nw',
		fade: false,
		opacity: 1,
		offset: 0
	}
};

//wrap them because of some jQuery Elements
$(document).ready(function() {


if($.fn.wl_Alert) $.fn.wl_Alert.defaults = {
	speed: 500,
	sticky: false,
	onBeforeClose: function (element) {},
	onClose: function (element) {}
};

if($.fn.wl_Autocomplete) $.fn.wl_Autocomplete.defaults = {
	//check http://jqueryui.com/demos/autocomplete/ for all options
};

if($.fn.wl_Breadcrump) $.fn.wl_Breadcrump.defaults = {
	start: 0,
	numbers: false,
	allownextonly: false,
	disabled: false,
	connect: null,
	onChange: function () {}
};

if($.fn.wl_Calendar) $.fn.wl_Calendar.defaults = {
	//check http://arshaw.com/fullcalendar/ for all options
};

if($.fn.wl_Chart) $.fn.wl_Chart.defaults = {
	width: null,
	height: 300,
	hideTable: true,
	data: {},
	stack: false,
	type: 'lines',
	points: null,
	shadowSize: 2,
	fill: null,
	fillColor: null,
	lineWidth: null,
	legend: true,
	legendPosition: "ne", // or "nw" or "se" or "sw"
	tooltip: true,
	tooltipGravity: 'n',
	tooltipPattern: function (value, legend, label, id) {
		return "value is " + value + " from " + legend + " at " + label + " (" + id + ")";
	},
	//tooltipPattern: "value is %1 from %2 at %3 (%4)", //also possible
	orientation: 'horizontal',
	colors: ['#b2e7b2', '#f0b7b7', '#b5f0f0', '#e8e8b3', '#efb7ef', '#bbb6f0'],
	flot: {},
	onClick: function (value, legend, label, id) {}
};

if($.fn.wl_Color) $.fn.wl_Color.defaults = {
	mousewheel: true,
	onChange: function (hsb, rgb) {}
};


if($.fn.wl_Date) $.fn.wl_Date.defaults = {
	value: null,
	mousewheel: true,
	
	//some datepicker standards
	dayNames : ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
	dayNamesMin : ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
	dayNamesShort : ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
	firstDay: 0,
	nextText: 'next',
	prevText: 'prev',
	currentText: 'Today',
	showWeek: true,
	dateFormat: 'mm/dd/yy'
};


if($.confirm) $.confirm.defaults = {
	text:{
		header: $.i18n._('js.wl.confirm.header'),
		ok: $.i18n._('js.wl.confirm.ok'),
		cancel: $.i18n._('js.wl.confirm.cancel')
	}
};
if($.prompt) $.prompt.defaults = {
	text:{
		header: $.i18n._('js.wl.prompt.header'),
		ok: $.i18n._('js.wl.prompt.ok'),
		cancel: $.i18n._('js.wl.prompt.cancel')
	}
};
if($.alert) $.alert.defaults = {
	nativ: false,
	resizable: false,
	modal: true,
	text:{
		header: $.i18n._('js.wl.alert.header'),
		ok: $.i18n._('js.wl.alert.ok')
	}
};

if($.fn.wl_Editor) $.fn.wl_Editor.defaults = {
	css: '/main-be/css/light/editor.css',
	buttons: 'bold|italic|underline|strikeThrough|justifyLeft|justifyCenter|justifyRight|justifyFull|highlight|colorpicker|indent|outdent|subscript|superscript|undo|redo|insertOrderedList|insertUnorderedList|insertHorizontalRule|createLink|insertImage|h1|h2|h3|h4|h5|h6|paragraph|rtl|ltr|cut|copy|paste|increaseFontSize|decreaseFontSize|html|code|removeFormat|insertTable',
	initialContent: ''
};

if($.fn.wl_File) $.fn.wl_File.defaults = {
	url: '/img/upload',
	autoUpload: true,
	paramName: 'files',
	multiple: false,
	allowedExtensions: ['jpg','jpeg','gif','png','doc','zip','docx','txt','pdf'],
	maxNumberOfFiles: 0,
	maxFileSize: 0,
	minFileSize: 0,
	sequentialUploads: false,
	dragAndDrop: true,
	formData: {},
	text: {
		ready: $.i18n._('js.wl.file.ready'),
		cancel: $.i18n._('js.wl.file.cancel'),
		remove: $.i18n._('js.wl.file.remove'),
		uploading: $.i18n._('js.wl.file.uploading'),
		done: $.i18n._('js.wl.file.done'),
		start: $.i18n._('js.wl.file.start'),
		add_files: $.i18n._('js.wl.file.add_files'),
		cancel_all: $.i18n._('js.wl.file.cancel_all'),
		remove_all: $.i18n._('js.wl.file.remove_all')
	},
	onAdd: function (e, data) {},
	onDelete:function(files){},
	onCancel:function(file){},
	onSend: function (e, data) {},
	onDone: function (e, data) {},
	onFinish: function (e, data) {},
	onFail: function (e, data) {},
	onAlways: function (e, data) {},
	onProgress: function (e, data) {},
	onProgressAll: function (e, data) {},
	onStart: function (e) {},
	onStop: function (e) {},
	onChange: function (e, data) {},
	onDrop: function (e, data) {},
	onDragOver: function (e) {},
	onFileError: function (error, fileobj) {
		$.msg('file is not allowed: ' + fileobj.name, {
			header: error.msg + ' (' + error.code + ')'
		});
	}
};

if($.fn.wl_Fileexplorer) $.fn.wl_Fileexplorer.defaults = {
	url: 'elfinder/php/connector.php',
	toolbar: [
		['back', 'reload', 'open', 'select', 'quicklook', 'info', 'rename', 'copy', 'cut', 'paste', 'rm', 'mkdir', 'mkfile', 'upload', 'duplicate', 'edit', 'archive', 'extract', 'resize', 'icons', 'list', 'help']
	]
};

if($.fn.wl_Form) $.fn.wl_Form.defaults = {
	submitButton: 'button.submit',
	resetButton: 'button.reset',
	method: 'post',
	action: null,
	ajax: true,
	serialize: false,
	parseQuery: true,
	dataType: 'text',
	status: true,
	sent: false,
	confirmSend: true,
	text: {
		required: $.i18n._('js.wl.form.required'),
		valid: $.i18n._('js.wl.form.valid'),
		password: $.i18n._('js.wl.form.password'),
		passwordmatch: $.i18n._('js.wl.form.passwordmatch'),
		fileinqueue: $.i18n._('js.wl.form.fileinqueue'),
		incomplete: $.i18n._('js.wl.form.incomplete'),
		send: $.i18n._('js.wl.form.send'),
		sendagain: $.i18n._('js.wl.form.sendagain'),
		success: $.i18n._('js.wl.form.success'),
		error: $.i18n._('js.wl.form.error'),
		parseerror: $.i18n._('js.wl.form.parseerror')
	},
	tooltip: {
		gravity: 'nw'
	},
	onRequireError: function (element) {},
	onValidError: function (element) {},
	onPasswordError: function (element) {},
	onFileError: function (element) {},
	onBeforePrepare: function () {},
	onBeforeSubmit: function (data) {},
	onReset: function () {},
	onComplete: function (textStatus, jqXHR) {},
	onError: function (textStatus, error, jqXHR) {},
	onSuccess: function (data, textStatus, jqXHR) {}
};

if($.fn.wl_Gallery) $.fn.wl_Gallery.defaults = {
	group: 'wl_gallery',
	fancybox: {},
	onEdit: function (element, href, title) {},
	onDelete: function (element, href, title) {}
};

if($.fn.wl_Multiselect) $.fn.wl_Multiselect.defaults = {
	height: 200,
	items: [],
	selected: [],
	showUsed: false,
	onAdd: function (values) {},
	onRemove: function (values) {},
	onSelect: function (values) {},
	onUnselect: function (values) {},
	onSort: function (values) {}
};

if($.fn.wl_Number) $.fn.wl_Number.defaults = {
	step: 1,
	decimals: 0,
	start: 0,
	min: null,
	max: null,
	mousewheel: true,
	onChange: function (value) {},
	onError: function (value) {}
};

if($.fn.wl_Password) $.fn.wl_Password.defaults = {
	confirm: true,
	showStrength: true,
	words: [$.i18n._('js.wl.pass.short'), $.i18n._('js.wl.pass.bad'), $.i18n._('js.wl.pass.medium'), $.i18n._('js.wl.pass.good'), $.i18n._('js.wl.pass.verygood'), $.i18n._('js.wl.pass.excellent')],
	minLength: 3,
	text: {
		confirm: $.i18n._('js.wl.pass.confirm'),
		nomatch: $.i18n._('js.wl.pass.nomatch')
	}
};

if($.fn.wl_Slider) $.fn.wl_Slider.defaults = {
	min: 0,
	max: 100,
	step: 1,
	animate: false,
	disabled: false,
	orientation: 'horizontal',
	range: false,
	mousewheel: true,
	connect: null,
	tooltip: false,
	tooltipGravity: ['s','w'],
	tooltipPattern: "%n",
	onSlide: function (value) {},
	onChange: function (value) {}
};

if($.fn.wl_Time) $.fn.wl_Time.defaults = {
	step: 5,
	timeformat: 24,
	roundtime: true,
	time: null,
	value: null,
	mousewheel: true,
	onDateChange: function (offset) {},
	onHourChange: function (offset) {},
	onChange: function (value) {}
};

if($.fn.wl_Valid) $.fn.wl_Valid.defaults = {
	errorClass: 'error',
	instant: true,
	regex: /.*/,
	minLength: 0,
	onChange: function ($this, value) {},
	onError: function ($this, value) {}
};

if($.fn.wl_Mail) $.fn.wl_Mail.defaults = {
	regex: /^([\w-]+(?:\.[\w-]+)*)\@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$|(\[?(\d{1,3}\.){3}\d{1,3}\]?)$/i,
	onChange: function (element, value) {
		element.val(value.toLowerCase());
	}
};

if($.fn.wl_URL) $.fn.wl_URL.defaults = {
	regex: /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w]))*\.+(([\w#!:.?+=&%@!\-\/]))?/,
	instant: false,
	onChange: function (element, value) {
		if (value != '' && !/^(ftp|http|https):\/\//.test(value)) element.val('http://' + value).trigger('change.wl_Valid');
	}
};

if($.fn.wl_Widget) $.fn.wl_Widget.defaults = {
	collapsed: false,
	load: null,
	reload: false,
	removeContent: true,
	collapsible: true,
	sortable: true,
	text: {
		loading: $.i18n._('js.wl.loading'),
		reload: $.i18n._('js.wl.reload'),
		collapse: $.i18n._('js.wl.collapse'),
		expand: $.i18n._('js.wl.expand')
	},
	onDrag: function () {},
	onDrop: function () {},
	onExpand: function () {},
	onCollapse: function () {}
};

});
