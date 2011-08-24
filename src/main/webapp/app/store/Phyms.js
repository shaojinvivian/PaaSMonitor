Ext.define('PaaSMonitor.store.Phyms', {
    extend: 'Ext.data.Store',
    model: 'PaaSMonitor.model.Phym',
    autoLoad: true,
    proxy: {
        type: 'rest',
        url: 'phyms',
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