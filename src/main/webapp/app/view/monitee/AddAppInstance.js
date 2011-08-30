Ext.define('PaaSMonitor.view.monitee.AddAppInstance', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.addAppInstance',

	title : 'Choose the Application Instances you want to add as monitees',
	store : 'AppInstances',

	initComponent : function() {
		this.items = [{
			xtype : 'listAppInstances'
		}, {
			xtype : 'button',
			text : 'Change'
		}];
		this.callParent(arguments);
	}
});
