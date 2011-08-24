package org.seforge.paas.monitor.web;

import org.seforge.paas.monitor.domain.AppInstance;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "appinstances", formBackingObject = AppInstance.class)
@RequestMapping("/appinstances")
@Controller
public class AppInstanceController {
}
