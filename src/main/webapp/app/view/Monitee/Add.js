/*
 * File: app/view/Monitee/Add.js
 *
 * This file was generated by Sencha Architect version 2.0.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.0.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.0.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('PaaSMonitor.view.Monitee.Add', {
    extend: 'Ext.window.Window',
    alias: 'widget.addmonitee',
    requires: [
        'PaaSMonitor.view.Monitee.PhymForm',
        'PaaSMonitor.view.Monitee.VimForm',
        'PaaSMonitor.view.Monitee.SaveVim',
        'PaaSMonitor.view.Monitee.AppServerForm',
        'PaaSMonitor.view.Monitee.SaveAppInstances'
    ],

    height: 250,
    width: 400,
    activeItem: 0,
    layout: {
        type: 'card'
    },
    closeAction: 'hide',
    modal: true,

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'moniteephymform'
                },
                {
                    xtype: 'moniteevimform'
                },
                {
                    xtype: 'savevim',
                    id: 'save_vim_panel'
                },
                {
                    xtype: 'moniteeappserverform'
                },
                {
                    xtype: 'moniteesaveappinstances',
                    id: 'save_app_instances_panel'
                }
            ]
        });

        me.callParent(arguments);
    }

});