package org.seforge.paas.monitor.web;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/appinstances")
@Controller
@RooWebJson(jsonObject = JmxAppInstance.class)
public class AppInstanceController {

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> deleteFromJson(@PathVariable("id") Long id) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            JmxAppInstance record = JmxAppInstance.findJmxAppInstance(id);
            record.remove();
            returnStatus = HttpStatus.OK;
            response.setMessage("AppInstance deleted.");
            response.setSuccess(true);
            response.setTotal(1L);
            response.setData(record);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").include("data.id").exclude("data.*").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        HttpStatus returnStatus = HttpStatus.OK;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            List<JmxAppInstance> records = JmxAppInstance.findAllJmxAppInstances();
            returnStatus = HttpStatus.OK;
            response.setMessage("All AppInstances retrieved.");
            response.setSuccess(true);
            response.setTotal(records.size());
            response.setData(records);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> createFromJson(@RequestBody String json) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            JmxAppInstance record = JmxAppInstance.fromJsonToJmxAppInstance(json);
            record.setId(null);
            record.setVersion(null);
            record.persist();
            returnStatus = HttpStatus.CREATED;
            response.setMessage("AppInstance created.");
            response.setSuccess(true);
            response.setTotal(1L);
            response.setData(record);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().include("data.vims").exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> updateFromJson(@RequestBody String json) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            JmxAppInstance record = JmxAppInstance.fromJsonToJmxAppInstance(json);
            JmxAppInstance mergedRecord = (JmxAppInstance) record.merge();
            if (mergedRecord == null) {
                returnStatus = HttpStatus.NOT_FOUND;
                response.setMessage("AppInstance update failed.");
                response.setSuccess(false);
                response.setTotal(0L);
            } else {
                returnStatus = HttpStatus.OK;
                response.setMessage("AppInstance updated.");
                response.setSuccess(true);
                response.setTotal(1L);
                response.setData(mergedRecord);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(params = "findAppInstances=ByAppServer", method = RequestMethod.GET)
    public ResponseEntity<java.lang.String> findAppInstancesByAppServerJson(@RequestParam("appServerId") Long appServerId) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            JmxAppServer appServer = JmxAppServer.findJmxAppServer(appServerId);
            List appInstances = JmxAppInstance.findJmxAppInstancesByJmxAppServer(appServer).getResultList();
            returnStatus = HttpStatus.OK;
            response.setMessage("All App Instances retrieved.");
            response.setSuccess(true);
            response.setTotal(appInstances.size());
            response.setData(appInstances);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(params = "findAppInstances=ByAppServers", method = RequestMethod.GET)
    public ResponseEntity<java.lang.String> findAppInstancesByAppServersJson(@RequestParam("appServerIdList") List<java.lang.String> appServerIdList) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            List<JmxAppInstance> data = new ArrayList<JmxAppInstance>();
            for (String appServerId : appServerIdList) {
                AppServer appServer = AppServer.findAppServer(Long.valueOf(appServerId));
                Set appInstances = ((JmxAppServer)appServer).getJmxAppInstances();
                data.addAll(appInstances);
            }
            returnStatus = HttpStatus.OK;
            response.setMessage("All App Instances retrieved.");
            response.setSuccess(true);
            response.setTotal(data.size());
            response.setData(data);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }
}
