Ext.define('PaaSMonitor.controller.Model', {
	extend : 'Ext.app.Controller',
	// stores : ['Phyms', 'Vims', 'MoniteeTree', 'AppServers', 'AppInstances'],
	// models : ['Phym', 'Vim', 'AppServer', 'AppInstance'],
	views : ['model.Diagram'],

	init : function() {
		this.control({			
			'modelDiagram' : {
				beforeactivate : this.draw
			}			
		});
	},
	
	draw : function() {
		/*
		container = document.getElementById('graphContainer');
		if (!mxClient.isBrowserSupported())
			{
				// Displays an error message if the browser is not supported.
				mxUtils.error('Browser is not supported!', 200, false);
			}
			else
			{
				// Enables crisp rendering of rectangles in SVG
				mxRectangleShape.prototype.crisp = true;
				
				// Creates the graph inside the given container
				var graph = new mxGraph(container);

				// Uncomment the following if you want the container
				// to fit the size of the graph
				//graph.setResizeContainer(true);
				
				// Enables rubberband selection
				new mxRubberband(graph);
				
				// Gets the default parent for inserting new cells. This
				// is normally the first child of the root (ie. layer 0).
				var parent = graph.getDefaultParent();
								
				// Adds cells to the model in a single step
				graph.getModel().beginUpdate();
				try
				{
					var v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
					var v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
					var e1 = graph.insertEdge(parent, null, '', v1, v2);
				}
				finally
				{
					// Updates the display
					graph.getModel().endUpdate();
				}
			}*/
			new mxApplication('config/diagrameditor.xml');
	}
	
});

/*
function onInit(editor)
		{
			// Crisp rendering in SVG except for connectors, actors, cylinder, ellipses
			mxShape.prototype.crisp = true;
			mxActor.prototype.crisp = false;
			mxCylinder.prototype.crisp = false;
			mxEllipse.prototype.crisp = false;
			mxDoubleEllipse.prototype.crisp = false;
			mxConnector.prototype.crisp = false;
			
			// Enables guides
			mxGraphHandler.prototype.guidesEnabled = true;
			
		    // Alt disables guides
		    mxGuide.prototype.isEnabledForEvent = function(evt)
		    {
		    	return !mxEvent.isAltDown(evt);
		    };
			
			// Enables snapping waypoints to terminals
			mxEdgeHandler.prototype.snapToTerminals = true;
			
			// Defines an icon for creating new connections in the connection handler.
			// This will automatically disable the highlighting of the source vertex.
			mxConnectionHandler.prototype.connectImage = new mxImage('images/connector.gif', 16, 16);
			
			// Enables connections in the graph and disables
			// reset of zoom and translate on root change
			// (ie. switch between XML and graphical mode).
			editor.graph.setConnectable(true);

			// Clones the source if new connection has no target
			editor.graph.connectionHandler.setCreateTarget(true);
			
			// Displays information about the session
			// in the status bar
			editor.addListener(mxEvent.SESSION, function(editor, evt)
			{
				var session = evt.getProperty('session');
				
				if (session.connected)
				{
					var tstamp = new Date().toLocaleString();
					editor.setStatus(tstamp+':'+
						' '+session.sent+' bytes sent, '+
						' '+session.received+' bytes received');
				}
				else
				{
					editor.setStatus('Not connected');
				}
			});
			
			// Updates the title if the root changes
			var title = document.getElementById('title');
			
			if (title != null)
			{
				var f = function(sender)
				{
					title.innerHTML = 'mxDraw - ' + sender.getTitle();
				};
				
				editor.addListener(mxEvent.ROOT, f);
				f(editor);
			}
			
		    // Changes the zoom on mouseWheel events
		    mxEvent.addMouseWheelListener(function (evt, up)
		    {
			    if (!mxEvent.isConsumed(evt))
			    {
			    	if (up)
					{
			    		editor.execute('zoomIn');
					}
					else
					{
						editor.execute('zoomOut');
					}
					
					mxEvent.consume(evt);
			    }
		    });

			// Defines a new action to switch between
			// XML and graphical display
			var textNode = document.getElementById('xml');
			var graphNode = editor.graph.container;
			var sourceInput = document.getElementById('source');
			sourceInput.checked = false;

			var funct = function(editor)
			{
				if (sourceInput.checked)
				{
					graphNode.style.display = 'none';
					textNode.style.display = 'inline';
					
					var enc = new mxCodec();
					var node = enc.encode(editor.graph.getModel());
					
					textNode.value = mxUtils.getPrettyXml(node);
					textNode.originalValue = textNode.value;
					textNode.focus();
				}
				else
				{
					graphNode.style.display = '';
					
					if (textNode.value != textNode.originalValue)
					{
						var doc = mxUtils.parseXml(textNode.value);
						var dec = new mxCodec(doc);
						dec.decode(doc.documentElement, editor.graph.getModel());
					}

					textNode.originalValue = null;
					
					// Makes sure nothing is selected in IE
					if (mxClient.IS_IE)
					{
						mxUtils.clearSelection();
					}

					textNode.style.display = 'none';

					// Moves the focus back to the graph
					textNode.blur();
					editor.graph.container.focus();
				}
			};
			
			editor.addAction('switchView', funct);
			
			// Client-side code for new image export
			var newExportImage = function(editor)
			{
    			var scale = 1;
    			var bounds = editor.graph.getGraphBounds();
    			
            	// Creates XML node to hold output
    			var xmlDoc = mxUtils.createXmlDocument();
    			var root = xmlDoc.createElement('output');
    			xmlDoc.appendChild(root);

    			// Creates interface for rendering output
    			var xmlCanvas = new mxXmlCanvas2D(root);
    			xmlCanvas.scale(scale);
    			xmlCanvas.translate(Math.round(-bounds.x * scale), Math.round(-bounds.y * scale));
    			
    			// Renders output to interface
    			var imgExport = new mxImageExport();
    			imgExport.drawState(editor.graph.getView().getState(editor.graph.model.root), xmlCanvas);

    			// Puts request data together
    			var filename = 'export.png';
    			var format = 'png';
    			var bg = '#FFFFFF';
    			var w = Math.round((bounds.width + 4) * scale);
    			var h = Math.round((bounds.height + 4) * scale);
    			var xml = mxUtils.getXml(root);
    			
    			// Compression is currently not used in this example
    			// Requires base64.js and redeflate.js
    			// xml = encodeURIComponent(Base64.encode(RawDeflate.deflate(xml), true));

				new mxXmlRequest('/New' + editor.urlImage.substring(1), 'filename=' + filename +
						'&format=' + format + '&bg=' + bg + '&w=' + w + '&h=' + h + '&xml=' +
						encodeURIComponent(xml)).simulate(document, '_blank');
			};
			
			editor.addAction('newExportImage', newExportImage);
			
			// Defines a new action to switch between
			// XML and graphical display
			mxEvent.addListener(sourceInput, 'click', function()
			{
				editor.execute('switchView');
			});

			// Create select actions in page
			var node = document.getElementById('mainActions');
			var buttons = ['group', 'delete', 'undo', 'print'];
			
			for (var i = 0; i < buttons.length; i++)
			{
				var button = document.createElement('button');
				mxUtils.write(button, mxResources.get(buttons[i]));
			
				var factory = function(name)
				{
					return function()
					{
						editor.execute(name);
					};
				};
			
				mxEvent.addListener(button, 'click', factory(buttons[i]));
				node.appendChild(button);
			}

			var select = document.createElement('select');
			var option = document.createElement('option');
			mxUtils.writeln(option, 'More Actions...');
			select.appendChild(option);

			var items = ['redo', 'ungroup', 'cut', 'copy', 'paste', 'show', 'exportImage', 'newExportImage'];

			for (var i=0; i<items.length; i++)
			{
				var option = document.createElement('option');
				mxUtils.writeln(option, mxResources.get(items[i]));
				option.setAttribute('value', items[i]);
				select.appendChild(option);
			}
			
			mxEvent.addListener(select, 'change', function(evt)
			{
				if (select.selectedIndex > 0)
				{
					var option = select.options[select.selectedIndex];
					select.selectedIndex = 0;
				
					if (option.value != null)
					{
						editor.execute(option.value);
					}
				}
			});
			
			node.appendChild(select);

			// Create select actions in page
			var node = document.getElementById('selectActions');
			mxUtils.write(node, 'Select: ');
			mxUtils.linkAction(node, 'All', editor, 'selectAll');
			mxUtils.write(node, ', ');
			mxUtils.linkAction(node, 'None', editor, 'selectNone');
			mxUtils.write(node, ', ');
			mxUtils.linkAction(node, 'Vertices', editor, 'selectVertices');
			mxUtils.write(node, ', ');
			mxUtils.linkAction(node, 'Edges', editor, 'selectEdges');

			// Create select actions in page
			var node = document.getElementById('zoomActions');
			mxUtils.write(node, 'Zoom: ');
			mxUtils.linkAction(node, 'In', editor, 'zoomIn');
			mxUtils.write(node, ', ');
			mxUtils.linkAction(node, 'Out', editor, 'zoomOut');
			mxUtils.write(node, ', ');
			mxUtils.linkAction(node, 'Actual', editor, 'actualSize');
			mxUtils.write(node, ', ');
			mxUtils.linkAction(node, 'Fit', editor, 'fit');
		}

		window.onbeforeunload = function() { return mxResources.get('changesLost'); };
		*/
