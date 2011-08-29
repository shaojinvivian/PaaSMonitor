Ext.define('PaaSMonitor.store.MoniteeTree', {
	extend : 'Ext.data.TreeStore',	
	autoLoad : true,
	proxy : {
		type : 'rest',
		url : 'monitees',
		headers : {
			'Accept' : 'application/json'
		}
	}
});
