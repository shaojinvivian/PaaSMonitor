Ext.define('PaaSMonitor.view.monitee.ConfigurePhyms', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.configurePhyms',	
	store : 'Phyms',
	title: 'Configure Phyms',

	initComponent : function() {
		this.items = [{
			xtype : 'box',
			id: 'box',
			html : '<p>There is no available phyms now!</p>'+
			'<p>Please add a new phym first</p>'
		}, {
			xtype : 'listPhyms'
		}, {
			xtype : 'button',
			text : 'Next'
		}];
		this.callParent(arguments);
	}
});
