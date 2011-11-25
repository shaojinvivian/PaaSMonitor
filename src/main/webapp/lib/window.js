Ext.require(['Ext.window.Window', 'Ext.tab.*', 'Ext.toolbar.Spacer', 'Ext.layout.container.Card', 'Ext.layout.container.Border']);

Ext.onReady(function() {
	var mappingWin;

	Ext.define('MBeanAttribute', {
		extend : 'Ext.data.Model',
		fields : [{
			name : 'name',
			type : 'string'
		}, {
			name : 'objectName',
			type : 'string'
		}, {
			name : 'version',
			type : 'string'
		}, {
			name : 'type',
			type : 'string'
		}]
	});

	var upperRightPanel = Ext.create('Ext.panel.Panel', {
		width : 350,
		id : 'upperRightPanel',
		layout : 'fit'

	});

	var gridStore = Ext.create('Ext.data.Store', {
		storeId : 'gridStore',
		model : 'MBeanAttribute'
	});

	var attributeGridPanel = Ext.create('Ext.grid.Panel', {
		id : 'attributeGridPanel',
		title : 'Added Mappings',
		columns : [{
			header : 'Name',
			dataIndex : 'name'
		}, {
			header : 'ObjectName',
			dataIndex : 'objectName'
		}, {
			header : 'Version',
			dataIndex : 'version'
		}, {
			header : 'Type',
			dataIndex : 'type'
		}],
		store : gridStore
	});

	var lowerRightPanel = Ext.create('Ext.panel.Panel', {
		id : 'lowerRightPanel',
		layout : 'vbox',
		items : [attributeGridPanel],
		height : 500
	});

	var rightPanel = Ext.create('Ext.panel.Panel', {
		width : 350,
		id : 'rightPanel',
		layout : {
			type : 'vbox',
			align : 'stretch',
			padding : 5
		},
		items : [upperRightPanel, lowerRightPanel]

	});

	var showForm = function(view, record) {
		if(record.get('leaf')) {
			var version = this.up('tabpanel').getActiveTab().id;
			var dnName = record.parentNode.data.text;
			var typeName = record.data.text;

			var attributesStore = Ext.create('Ext.data.Store', {
				model : 'MBeanAttribute',
				proxy : {
					type : 'ajax',
					extraParams : {
						version : version,
						dnName : dnName,
						typeName : typeName
					},
					url : 'jmx/mbeanattributes',
					reader : {
						type : 'json',
						root : 'data'
					}
				},
				autoLoad : true

			});

			var attribute = Ext.create('Ext.form.ComboBox', {
				fieldLabel : 'Attribute',
				store : attributesStore,
				queryMode : 'local',
				displayField : 'name',				
				// valueField : 'type'
			});

			var ajax = Ext.Ajax.request({
				url : 'jmx/mbeaninfo',
				params : {
					version : version,
					dnName : dnName,
					typeName : typeName
				},
				method : 'get',
				success : function(response) {
					upperRightPanel.removeAll();

					var treeDetail = Ext.create('widget.form', {
						title : 'MBean Detail',
						bodyPadding : 20,
						width : 350,
						autoheight : true,
						layout : 'anchor',
						defaults : {
							anchor : '100%'
						},
						defaultType : 'textfield',
						buttons : [{
							text : 'Reset',
							handler : function() {
								this.up('form').getForm().reset();
							}
						}, {
							text : 'Add',
							formBind : true, // only enabled once the
							// form is valid
							disabled : true,
							handler : function() {
								var form = this.up('form').getForm();
								if(form.isValid()) {
									var newItem = Ext.create('MBeanAttribute');
									var objectName = dnName + ':type='+ typeName +  ',' ;									
									newItem.set('name', attribute.displayTplData[0].name);
									newItem.set('objectName', objectName);
									newItem.set('version', version);
									newItem.set('type', attribute.displayTplData[0].type)
									gridStore.add(newItem);
								}
							}
						}]
					});
					treeDetail.add(Ext.decode(response.responseText).data);
					treeDetail.add(attribute);
					treeDetail.doLayout();
					upperRightPanel.add(treeDetail);
				}
			});
		}

	};
	var tomcat6Tree = Ext.create('Ext.tree.Panel', {
		id : 'tomcat6',
		title : 'Tomcat 6',
		height : 300,
		width : 200,
		rootVisible : false,
		autoScroll : true,
		collapsible : true,
		store : Ext.create('Ext.data.TreeStore', {
			proxy : {
				type : 'ajax',
				url : 'data/tomcat6-tree.txt'
			},
			root : {
				expanded : true
			}
		}),
		listeners : {
			'itemclick' : showForm
		}
	});

	var tomcat7Tree = Ext.create('Ext.tree.Panel', {
		id : 'tomcat7',
		title : 'Tomcat 7',
		height : 300,
		width : 200,
		rootVisible : false,
		autoScroll : true,
		collapsible : true,
		store : Ext.create('Ext.data.TreeStore', {
			proxy : {
				type : 'ajax',
				url : 'data/tomcat7-tree.txt'
			},
			root : {
				expanded : true
			}
		}),
		listeners : {
			'itemclick' : showForm
		}
	});

	var jettyTree = Ext.create('Ext.tree.Panel', {
		id : 'jetty8',
		title : 'Jetty 8',
		height : 300,
		width : 200,
		rootVisible : false,
		autoScroll : true,
		collapsible : true,
		store : Ext.create('Ext.data.TreeStore', {
			proxy : {
				type : 'ajax',
				url : 'data/jetty-tree.txt'
			},
			root : {
				expanded : true
			}
		}),
		listeners : {
			'itemclick' : showForm
		}
	});

	if(mappingWin == null) {
		mappingWin = Ext.create('widget.window', {
			title : 'Please choose the mapping',
			closable : true,
			closeAction : 'hide',
			//animateTarget: this,
			width : 650,
			height : 400,
			layout : {
				type : 'hbox', // Arrange child items vertically
				align : 'stretch', // Each takes up full width
				padding : 5

			},
			bodyStyle : 'padding: 5px;',
			items : [{
				xtype : 'tabpanel',
				width : 250,
				items : [tomcat6Tree, tomcat7Tree, jettyTree],
				margin : '0, 10, 0, 0'
			}, rightPanel]
		});
	}
	mappingWin.show();

});
