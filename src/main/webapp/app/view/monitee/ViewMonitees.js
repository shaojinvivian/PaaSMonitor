Ext.define('PaaSMonitor.view.monitee.ViewMonitees', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.viewMonitees',
	title : 'All Monitees',
	store : 'MoniteeTree',
	collapsible : true,
	useArrows : true,
	rootVisible : false,

	initComponent : function() {

		this.callParent(arguments);
	}
});
