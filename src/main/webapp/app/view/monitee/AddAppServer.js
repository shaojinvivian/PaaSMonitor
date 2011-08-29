Ext.define('PaaSMonitor.view.monitee.AddAppServer', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.addAppServer',

	title : 'Add a new Application Server',
	store : 'AppServers',

	initComponent : function() {
		this.items = [{
			xtype : 'box',
			id: 'box',
			html : 'Hello world!'
		}, {
			xtype : 'chooseAppServer'
		}];

		this.callParent(arguments);
	}
});
