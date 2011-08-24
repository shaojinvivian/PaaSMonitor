Ext.define('PaaSMonitor.store.MoniteeTree', {
	extend : 'Ext.data.TreeStore',	
	autoLoad : true,
	proxy : {
		type : 'rest',
		url : 'phyms',
		headers : {
			'Accept' : 'application/json'
		}
	}
});
