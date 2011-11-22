Ext.require([
    'Ext.window.Window',
    'Ext.tab.*',
    'Ext.toolbar.Spacer',
    'Ext.layout.container.Card',
    'Ext.layout.container.Border'
]);

Ext.onReady(function(){	
var mappingWin;
   var tomcat6Tree = Ext.create('Ext.tree.Panel', {
		id : 'tomcat6Tree',
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
		})
	});

	var tomcat7Tree = Ext.create('Ext.tree.Panel', {
		id : 'tomcat7Tree',
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
		})
	});

	var jettyTree = Ext.create('Ext.tree.Panel', {
		id : 'jettyTree',
		title : 'Jetty',
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
		})
	});
	
	
	var treeDetail = Ext.create('Ext.form.Panel', {
    title: 'Simple Form',
    bodyPadding: 20,
    width: 350,
    height: 500,

    // The form will submit an AJAX request to this URL when submitted
    

    // Fields will be arranged vertically, stretched to full width
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },

    // The fields
    defaultType: 'textfield',


    // Reset and Submit buttons
    buttons: [{
        text: 'Reset',
        handler: function() {
            this.up('form').getForm().reset();
        }
    }, {
        text: 'Submit',
        formBind: true, //only enabled once the form is valid
        disabled: true,
        handler: function() {
            var form = this.up('form').getForm();
            if (form.isValid()) {
                form.submit({
                    success: function(form, action) {
                       Ext.Msg.alert('Success', action.result.msg);
                    },
                    failure: function(form, action) {
                        Ext.Msg.alert('Failed', action.result.msg);
                    }
                });
            }
        }
    }]
});


	
	var ajax = Ext.Ajax.request({
  url: 'data/formdata.txt',
  method: 'get',
  success: function(response) {               
    // response = Ext.JSON.decode(response.responseText);
    treeDetail.add(Ext.decode(response.responseText).data);
	treeDetail.doLayout();
	}
});




	if(mappingWin==null){
		mappingWin = Ext.create('widget.window', {
			title : 'Please choose the mapping',
			closable : true,
			closeAction : 'hide',
			//animateTarget: this,
			width : 900,
			height : 350,
			layout : 'column',
			bodyStyle : 'padding: 5px;',
			items : [{				
				xtype : 'tabpanel',
				items : [tomcat6Tree, tomcat7Tree, jettyTree]
			},treeDetail]
		});
	}	
	mappingWin.show();

	
});

