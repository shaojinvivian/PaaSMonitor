// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seforge.paas.monitor.domain.AppInstanceDataOnDemand;
import org.seforge.paas.monitor.domain.AppInstanceIntegrationTest;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect AppInstanceIntegrationTest_Roo_IntegrationTest {
    
    declare @type: AppInstanceIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: AppInstanceIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml");
    
    declare @type: AppInstanceIntegrationTest: @Transactional;
    
    @Autowired
    private AppInstanceDataOnDemand AppInstanceIntegrationTest.dod;
    
    @Test
    public void AppInstanceIntegrationTest.testCountJmxAppInstances() {
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", dod.getRandomJmxAppInstance());
        long count = JmxAppInstance.countJmxAppInstances();
        Assert.assertTrue("Counter for 'JmxAppInstance' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void AppInstanceIntegrationTest.testFindJmxAppInstance() {
        JmxAppInstance obj = dod.getRandomJmxAppInstance();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to provide an identifier", id);
        obj = JmxAppInstance.findJmxAppInstance(id);
        Assert.assertNotNull("Find method for 'JmxAppInstance' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'JmxAppInstance' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void AppInstanceIntegrationTest.testFindAllJmxAppInstances() {
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", dod.getRandomJmxAppInstance());
        long count = JmxAppInstance.countJmxAppInstances();
        Assert.assertTrue("Too expensive to perform a find all test for 'JmxAppInstance', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<JmxAppInstance> result = JmxAppInstance.findAllJmxAppInstances();
        Assert.assertNotNull("Find all method for 'JmxAppInstance' illegally returned null", result);
        Assert.assertTrue("Find all method for 'JmxAppInstance' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void AppInstanceIntegrationTest.testFindJmxAppInstanceEntries() {
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", dod.getRandomJmxAppInstance());
        long count = JmxAppInstance.countJmxAppInstances();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<JmxAppInstance> result = JmxAppInstance.findJmxAppInstanceEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'JmxAppInstance' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'JmxAppInstance' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void AppInstanceIntegrationTest.testFlush() {
        JmxAppInstance obj = dod.getRandomJmxAppInstance();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to provide an identifier", id);
        obj = JmxAppInstance.findJmxAppInstance(id);
        Assert.assertNotNull("Find method for 'JmxAppInstance' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyJmxAppInstance(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'JmxAppInstance' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void AppInstanceIntegrationTest.testMergeUpdate() {
        JmxAppInstance obj = dod.getRandomJmxAppInstance();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to provide an identifier", id);
        obj = JmxAppInstance.findJmxAppInstance(id);
        boolean modified =  dod.modifyJmxAppInstance(obj);
        Integer currentVersion = obj.getVersion();
        JmxAppInstance merged = (JmxAppInstance)obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'JmxAppInstance' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void AppInstanceIntegrationTest.testPersist() {
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", dod.getRandomJmxAppInstance());
        JmxAppInstance obj = dod.getNewTransientJmxAppInstance(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'JmxAppInstance' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'JmxAppInstance' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void AppInstanceIntegrationTest.testRemove() {
        JmxAppInstance obj = dod.getRandomJmxAppInstance();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'JmxAppInstance' failed to provide an identifier", id);
        obj = JmxAppInstance.findJmxAppInstance(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'JmxAppInstance' with identifier '" + id + "'", JmxAppInstance.findJmxAppInstance(id));
    }
    
}
