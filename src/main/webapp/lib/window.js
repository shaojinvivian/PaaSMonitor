Ext.require(['Ext.window.Window', 'Ext.tab.*', 'Ext.toolbar.Spacer',
		'Ext.layout.container.Card', 'Ext.layout.container.Border']);

Ext.onReady(function() {
	var mappingWin;

	var detailPanel = Ext.create('Ext.panel.Panel', {	
			width: 350
				
			});

	var showForm = function(view, record) {
		if (record.get('leaf')) {
			var version = this.up('tabpanel').getActiveTab().id;
			var dnName = record.parentNode.data.text;
			var typeName = record.data.text;

			var ajax = Ext.Ajax.request({
				url : 'jmx/mbeaninfo',
				params : {
					version : version,
					dnName : dnName,
					typeName : typeName
				},
				method : 'get',
				success : function(response) {
					// response = Ext.JSON.decode(response.responseText);
					detailPanel.removeAll();
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
									text : 'Submit',
									formBind : true, // only enabled once the
									// form is valid
									disabled : true,
									handler : function() {
										var form = this.up('form').getForm();
										if (form.isValid()) {
											form.submit({
												success : function(form, action) {
													Ext.Msg.alert('Success',
															action.result.msg);
												},
												failure : function(form, action) {
													Ext.Msg.alert('Failed',
															action.result.msg);
												}
											});
										}
									}
								}]
					});
					treeDetail.add(Ext.decode(response.responseText).data);
					treeDetail.doLayout();
					detailPanel.add(treeDetail);
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

	if (mappingWin == null) {
		mappingWin = Ext.create('widget.window', {
					title : 'Please choose the mapping',
					closable : true,
					closeAction : 'hide',
					//animateTarget: this,
					width : 650,
					height : 350,
					layout: {
        type: 'hbox',       // Arrange child items vertically
        align: 'stretch',    // Each takes up full width
        padding: 5
        
    },
					bodyStyle : 'padding: 5px;',
					items : [{
								xtype : 'tabpanel',		
								width : 250,
								items : [tomcat6Tree, tomcat7Tree, jettyTree],
								margin: '0, 10, 0, 0'
							}, detailPanel]
				});
	}
	mappingWin.show();

});
