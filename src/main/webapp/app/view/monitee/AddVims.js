Ext.define('PaaSMonitor.view.monitee.AddVims', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.addVims',
	initComponent : function() {
		this.items = [{
			xtype : 'listVimsByPhym'
		}, {
			xtype : 'button',
			text : 'Save'
		}];
		this.callParent(arguments);
	}
});
