// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.seforge.paas.monitor.domain.App;
import org.seforge.paas.monitor.domain.AppInstance;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect AppController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String AppController.create(@Valid App app, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("app", app);
            return "apps/create";
        }
        uiModel.asMap().clear();
        app.persist();
        return "redirect:/apps/" + encodeUrlPathSegment(app.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String AppController.createForm(Model uiModel) {
        uiModel.addAttribute("app", new App());
        return "apps/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String AppController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("app", App.findApp(id));
        uiModel.addAttribute("itemId", id);
        return "apps/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String AppController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("apps", App.findAppEntries(firstResult, sizeNo));
            float nrOfPages = (float) App.countApps() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("apps", App.findAllApps());
        }
        return "apps/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String AppController.update(@Valid App app, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("app", app);
            return "apps/update";
        }
        uiModel.asMap().clear();
        app.merge();
        return "redirect:/apps/" + encodeUrlPathSegment(app.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String AppController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("app", App.findApp(id));
        return "apps/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String AppController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        App app = App.findApp(id);
        app.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/apps";
    }
    
    @ModelAttribute("apps")
    public Collection<App> AppController.populateApps() {
        return App.findAllApps();
    }
    
    @ModelAttribute("appinstances")
    public Collection<AppInstance> AppController.populateAppInstances() {
        return AppInstance.findAllAppInstances();
    }
    
    String AppController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
