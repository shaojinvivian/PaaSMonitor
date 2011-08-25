Ext.define('PaaSMonitor.view.monitee.AddAppServer', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.addAppServer',

	title : 'Add a new Application Server',
	store : 'AppServers',

	initComponent : function() {
		this.items = [{
			xtype : 'form',	
			bodyPadding: 10,		
			items : [{
				xtype : 'textfield',
				name : 'name',
				fieldLabel : 'Name'
			}, {
				xtype : 'textfield',
				name : 'ip',
				fieldLabel : 'IP'
			}, {
				xtype : 'textfield',
				name : 'jmxPort',
				fieldLabel : 'JMX Port'			
			}],
			
			buttons : [{
				text : 'Save',
				action : 'save'
			}, {
				text : 'Cancel',
				scope : this,
				handler : this.close
			}],			
			
		},{
			xtype: 'chooseAppServer'
		}];

		this.callParent(arguments);
	}
});
