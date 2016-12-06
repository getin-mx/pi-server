/**
 * IndoorMap.js
 * 
 * Library for making map location points inside a venue. It uses a png map, and
 * a series of positioning points to show the specific recorded points. It also
 * crosses this series of points with a heat map generator to specify the amount
 * of type that each person spent in a specific location
 */

// Global variables
var _startX = 0;            // mouse starting positions
var _startY = 0;
var _offsetX = 0;           // current element offset
var _offsetY = 0;
var _dragElement;           // needs to be passed from OnMouseDown to OnMouseMove
var _oldZIndex = 0;         // we temporarily increase the z-index during drag
var _debug = $('debug');    // makes life easier

// Encapsulated Library Definition
indoormap = {

		/**
		 * Obtains the browser query string
		 * 
		 * @returns an array with all the parameters sent in the browser query
		 *          string
		 */
		queryString : function () {
			// This function is anonymous, is executed immediately and 
			// the return value is assigned to QueryString!
			var query_string = {};
			var query = window.location.search.substring(1);
			var vars = query.split("&");
			for (var i=0;i<vars.length;i++) {
				var pair = vars[i].split("=");
				// If first entry with this name
				if (typeof query_string[pair[0]] === "undefined") {
					query_string[pair[0]] = pair[1];
					// If second entry with this name
				} else if (typeof query_string[pair[0]] === "string") {
					var arr = [ query_string[pair[0]], pair[1] ];
					query_string[pair[0]] = arr;
					// If third or later entry with this name
				} else {
					query_string[pair[0]].push(pair[1]);
				}
			} 
			return query_string;
		},

		/**
		 * Initialize the library defining the mousedown and mouseup methods
		 * used to track the points movement
		 */
		init: function() {

			/**
			 * Mouse down event (start drag)
			 */
			document.onmousedown = function(e) {
				// IE is retarded and doesn't pass the event object
				if (e == null) 
					e = window.event; 

				// IE uses srcElement, others use target
				var target = e.target != null ? e.target : e.srcElement;

				// for IE, left click == 1
				// for Firefox, left click == 0
				if ((e.button == 1 && window.event != null || 
						e.button == 0) && 
						target.className == 'draggable')
				{
					// grab the mouse position
					_startX = e.clientX;
					_startY = e.clientY;

					// grab the clicked element's position
					_offsetX = indoormap.extractNumber(target.style.left);
					_offsetY = indoormap.extractNumber(target.style.top);

					// bring the clicked element to the front while it is being dragged
					_oldZIndex = target.style.zIndex;
					target.style.zIndex = 10000;

					// we need to access the element in OnMouseMove
					_dragElement = target;

					// tell our code to start moving the element with the mouse
					document.onmousemove = function(e) {
						if (e == null) 
							var e = window.event; 

						// this is the actual "drag code"
						_dragElement.style.left = (_offsetX + e.clientX - _startX) + 'px';
						_dragElement.style.top = (_offsetY + e.clientY - _startY) + 'px';
					};

					// cancel out any text selections
					document.body.focus();

					// prevent text selection in IE
					document.onselectstart = function () { return false; };
					// prevent IE from trying to drag an image
					target.ondragstart = function() { return false; };

					// prevent text selection (except IE)
					return false;
				}
			};

			/**
			 * Mouse Up Event (Stop drag)
			 */
			document.onmouseup = function(e) {
				if (_dragElement != null) {
					_dragElement.style.zIndex = _oldZIndex;

					// we're done with these events until the next OnMouseDown
					document.onmousemove = null;
					document.onselectstart = null;
					_dragElement.ondragstart = null;

					// this is how we know we're not dragging      
					_dragElement = null;
				}
			};
		},

		/**
		 * Loads a FloorMap object with the data point contents, and translate
		 * the info as a javascript object
		 * 
		 * @param identifier
		 *            The Map element to load. This is the FloorMap object
		 *            identifier to look for, as the rest of the URL is dictated
		 *            by hardcode.
		 * @returns The loaded and parsed data set
		 */
		loadAndParseURL: function(identifier) {
			var dataReturn = {};

			url = '/main-be/doGetFloorMapData?floorMapId=' + identifier;

			$.ajax({
				url:        url,
				async:      false,
				cache:		false,
				contentType:'text/plain',
				dataType:   'text',
				success:    function(data, status) {
					dataReturn = indoormap.parseData(data); 
				},
				error:		function() {},
				failure:	function() {}
			});

			return dataReturn;
		},

		/**
		 * Loads a local file or URL that contains the FloorMap object with the
		 * data point contents, and translate the info as a javascript object
		 * 
		 * @param filename
		 *            The file name or url to load
		 * @returns The loaded and parsed data set
		 */
		loadAndParseFile: function(filename) {
			var dataReturn = {};

			$.ajax({
				url:        filename,
				async:      false,
				cache:		false,
				contentType:'text/plain',
				dataType:   'text',
				success:    function(data, status) {
					dataReturn = indoormap.parseData(data); 
				},
				error:		function() {},
				failure:	function() {}
			});

			return dataReturn;
		},

		/**
		 * Parses raw data loaded with information about WifiSpots
		 * 
		 * @param json
		 *            The json content to parse
		 * @returns A full formed javascript object with the parsed FloorMap
		 *          information
		 */
		parseData: function(json) {
			var data = JSON.parse(json);
			if( data.corrected == undefined )
				data.corrected = false;
			return data;
		},

		/**
		 * Saves the working set in the data server
		 * 
		 * @param data
		 *            The object with the FloorMap data set
		 * @param identifier
		 *            The FloorMap object identifier
		 * @param success
		 *            A success callback function
		 * @param failure
		 *            An error callback function
		 */
		save: function(data, identifier, success, failure) {

			url = '/main-be/doUpdateFloorMapData?floorMapId=' + identifier;

			for( var i = 0; i < data.data.length; i++ ) {
				var element = data.data[i];
				element.x = indoormap.extractNumber($('#' + element.uid).css('left'));
				element.y = indoormap.extractNumber($('#' + element.uid).css('top'));
				element.zoneName = $('#zoneName-' + element.uid).val();
				data.data[i] = element;
			}

			$.ajax({
				headers: {"Content-Type":"application/json; charset=UTF-8"},
				crossDomain : true,
				dataType : "json",
				type : 'POST',
				url : url,
				data : JSON.stringify(data),
				success : function(data) {
					alert('Datos Guardados');
					if( typeof(success) == 'function') success();
				},
				error : function() {
					alert('Error al guardar los datos. Intente de nuevo');
					if( typeof(failure) == 'function') failure();
				}
			});
		},

		/**
		 * The information obtained with a tablet is generally recorded with a
		 * different map resolution and size than the map we are showing. This
		 * function converts the points x and y coordinates according to the
		 * final map resolution
		 * 
		 * @param data
		 *            A FloorMap data set
		 * @returns The corrected FloorMap data set
		 */
		correct: function(data) {

			var sourceMapHeight = data.screenHeight - data.marginTop;
			var sourceMapWidth  = data.screenWidth;

			if( sourceMapHeight == 0 ) {
				sourceMapHeight = data.mapHeight;
				data.screenHeight = data.mapHeight;
				data.marginTop = 0;
			}
			if( sourceMapWidth  == 0 ) {
				sourceMapWidth = data.mapWidth;
				data.screenWidth = data.mapWidth;
			}
			
			for( var i = 0; i < data.data.length; i++ ) {
				var element = data.data[i];
				var y1 = (element.y - data.marginTop) * 100 / sourceMapHeight;
				var y2 = Math.floor(y1 * data.mapHeight / 100);
				var x1 = element.x * 100 / sourceMapWidth;
				var x2 = Math.floor(x1 * data.mapWidth / 100);

				element.y = y2;
				element.x = x2;

				data.data[i] = element;
			}

			data.corrected = true;
			return data;

		},

		/**
		 * Draws a heat map over the data points.
		 * 
		 * @param mapData
		 *            The FloorMap data set
		 * @param identifier
		 *            The FloorMap identifier
		 * @param canvas
		 *            An HTML canvas to work with
		 */
		drawHeatMap: function(mapData, identifier, canvas) {
			
			url = '/main-be/metrics/chartData?metricEntity=VisitorFloorMap&lastMonth=true&metricType=4&filterNames=wifiSpot&entityKind=15&entityId=' + identifier;
			$.getJSON(url + '&callback=?', function(data) {

				var heatmap = h337.create({
					container: canvas,
					radius: 150,
					maxOpacity: .7,
					minOpacity: 0,
					blur: .75				
				});

				heatData = [];
				
				for(var i = 0; i < data.length; i++) {
					var ele = data[i];
					heatData.push(indoormap.prepareHeatElement(ele[0], ele[1]));
				}
				
				heatmap.setData({
					max: 4,
					data: heatData
				});
			});

		},

		/**
		 * Takes data from a WifiSpot position to prepare a Heat Map element
		 * 
		 * @param id
		 *            The WifiSpot object identifier
		 * @param percentage
		 *            the percentage value received from the heat map data
		 *            service
		 * @returns A heat map element, composed by x, y, and value
		 */
		prepareHeatElement: function(id, percentage) {
			var srcPoint = $('#' + id);
			obj = {
					x: Math.floor(indoormap.extractNumber(srcPoint.css('left')) + (indoormap.extractNumber(srcPoint.css('width'))  / 2)),
					y: Math.floor(indoormap.extractNumber(srcPoint.css('top'))  + (indoormap.extractNumber(srcPoint.css('height')) / 2)),
					value: Math.ceil(percentage)
			};
			
			return obj;
		},

		/**
		 * Draws the WifiSpot elements in the map container
		 * 
		 * @param data
		 *            The FloorMap data set that contains the WifiSpot elements
		 *            inside
		 * @param container
		 *            Where to draw the WifiSpot element
		 */
		draw: function(data, container) {

			for( var i = 0; i < data.data.length; i++ ) {
				var element = data.data[i];
				console.log(element);
				indoormap.addLocation(element.uid, element.x, element.y, container);
			}

		},

		/**
		 * Adds an individual WifiSpot on the div map container
		 * 
		 * @param id
		 *            WifiSpot Identifier
		 * @param x
		 *            x coordinate
		 * @param y
		 *            y coordinate
		 * @param div
		 *            Where to show the spot
		 */
		addLocation: function(id, x, y, div) {
			var img = new Image();
			img.id = id;
			img.src = '/main-be/css/images/bullet.gif';
			div.appendChild(img);
			$('#' + id).addClass('draggable');
			$('#' + id).css('left', x + 'px');
			$('#' + id).css('top', y + 'px');
		},

		/**
		 * Simple parser to convert a string to a number without the risk of
		 * receiving a NaN
		 * 
		 * @param value
		 *            The value to parse
		 * @returns a valid number, never a NaN
		 */
		extractNumber: function(value) {
			var n = parseInt(value);
			return n == null || isNaN(n) ? 0 : n;
		},

		onfocus: function(elementId, data) {
			console.log('focus on ' + elementId);

			var div = $('#' + elementId); 
			div[0].src = '/main-be/css/images/bullet2.gif';
		},
		
		onfocusout: function(elementId, data) {
			console.log('blur on ' + elementId);

			var div = $('#' + elementId); 
			div[0].src = '/main-be/css/images/bullet.gif';
		},
};
