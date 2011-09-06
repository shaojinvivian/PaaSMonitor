Ext.define('PaaSMonitor.view.monitee.ConfigureAppServer', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.configureAppServer',	
	store : 'AppServers',
	title: 'Configure App Servers',

	initComponent : function() {
		this.items = [{
			xtype : 'box',
			id: 'noAppServerBox',
			html : '<p>There is no available app servers now!</p>'+
			'<p>Please add a new app server by starting it with an agent</p>'
		}, {
			xtype : 'listAppServers'
		}, {
			xtype : 'button',
			text : 'Next'
		}];
		this.height = 500;
		this.callParent(arguments);
	}
});
