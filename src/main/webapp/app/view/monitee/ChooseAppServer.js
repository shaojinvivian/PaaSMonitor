Ext.define('PaaSMonitor.view.monitee.ChooseAppServer', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.chooseAppServer',

	title : 'Choose the Application Servers you want to add as monitees',
	store : 'AppServers',

	initComponent : function() {
		this.items = [{
			xtype : 'listAppServers'
		}, {
			xtype : 'button',
			text : 'Change'
		}];
		this.callParent(arguments);
	}
});
