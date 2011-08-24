Ext.define('PaaSMonitor.store.AppServers', {
    extend: 'Ext.data.Store',
    model: 'PaaSMonitor.model.AppServer',
    autoLoad: true,
    proxy: {
        type: 'rest',
        url: 'appservers',
        headers: {
        	'Accept': 'application/json'
        },        
        reader: {
            type: 'json',
            root: 'data',
            successProperty: 'success'
        }
    }
});