package com.sanguine.webpms.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpms.bean.clsPropertySetupBean;
import com.sanguine.webpms.model.clsPropertySetupHdModel;
import com.sanguine.webpms.service.clsPropertySetupService;

@Controller
public class clsPropertySetupController
{   
    @Autowired
    private clsPropertySetupService objPropertySetupService;
    
    @Autowired
    private clsGlobalFunctionsService objGlobalFunctionsService;
    
    @Autowired
    private clsGlobalFunctions objGlobal;

    
// Open PropertySetup
    @RequestMapping(value = "/frmPropertySetup", method = RequestMethod.GET)
    public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
    {
		String urlHits = "1";
		 String clientCode = request.getSession().getAttribute("clientCode").toString();
		 String propertyCode = request.getSession().getAttribute("propertyCode").toString();
		try
		{
		    urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
		    urlHits = "1";
		}
		model.put("urlHits", urlHits);
		List listOfProperty = objGlobalFunctionsService.funGetList("select strPropertyName from dbwebmms.tblpropertymaster");
		model.put("listOfProperty", listOfProperty);
		
		clsPropertySetupHdModel objModel = objPropertySetupService.funGetPropertySetup(propertyCode, clientCode);
//				objGlobalFunctionsService.funGetList("select tmeCheckInTime,tmeCheckOutTime from tblpropertysetup   "
//				+ " where strPropertyCode='"+propertyCode+"' and strClientCode='"+clientCode+"' ");
		if(objModel==null)
		{
			model.put("checkInTime", "00:01:00");
			model.put("checkOutTime", "23:59:00");
		}else
		{
			model.put("checkInTime", objModel.getTmeCheckInTime());
			model.put("checkOutTime", objModel.getTmeCheckOutTime());
		}
		
		if ("2".equalsIgnoreCase(urlHits))
		{
		    return new ModelAndView("frmPropertySetup_1", "command", new clsPropertySetupBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
		    return new ModelAndView("frmPropertySetup", "command", new clsPropertySetupBean());
		}
		else
		{
		    return null;
		}
    }
    
    
// Load Master Data On Form
    @RequestMapping(value = "/frmPropertySetup1", method = RequestMethod.POST)
    public @ResponseBody clsPropertySetupHdModel funLoadMasterData(HttpServletRequest request)
    {
    	return null;
    }
    
    
// Save or Update PropertySetup
    @RequestMapping(value = "/savePropertySetup", method = RequestMethod.POST)
    public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPropertySetupBean objBean, BindingResult result, HttpServletRequest req)
    {
		String urlHits = "1";
		try
		{
		    urlHits = req.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
		    urlHits = "1";
		}
		if (!result.hasErrors())
		{
		    String clientCode = req.getSession().getAttribute("clientCode").toString();
		    String userCode = req.getSession().getAttribute("usercode").toString();
		    objPropertySetupService.funAddUpdatePropertySetup(funPrepareModel(objBean, userCode, clientCode));
		    req.getSession().setAttribute("success", true);
		    req.getSession().setAttribute("successMessage", "Property Code. : ".concat(objBean.getStrPropertyCode()));
		    return new ModelAndView("redirect:/frmPropertySetup.html?saddr=" + urlHits);
		}
		else
		{
		    return new ModelAndView("frmPropertySetup?saddr=" + urlHits);
		}
    }
    
    
// Convert bean to model function
    private clsPropertySetupHdModel funPrepareModel(clsPropertySetupBean objBean, String userCode, String clientCode)
    {
		clsPropertySetupHdModel objPropertySetupModel=new clsPropertySetupHdModel();
		List listProperties=objGlobalFunctionsService.funGetList("select strPropertyCode from dbwebmms.tblpropertymaster "
			+ " where strPropertyName='"+objBean.getStrPropertyCode()+"'");
		String propertyCode="";
		if(listProperties.size()>0)
		{
		    propertyCode=listProperties.get(0).toString();
		}
		objPropertySetupModel.setStrPropertyCode(propertyCode);
		objPropertySetupModel.setStrClientCode(clientCode);
		objPropertySetupModel.setTmeCheckInTime(objBean.getTmeCheckInTime());
		objPropertySetupModel.setTmeCheckOutTime(objBean.getTmeCheckOutTime());
		
		objPropertySetupModel.setStrSMSProvider(objBean.getStrSMSProvider());
		objPropertySetupModel.setStrReservationSMSContent(objBean.getStrReservationSMSContent());
		objPropertySetupModel.setStrCheckInSMSContent (objBean.getStrCheckInSMSContent());
		objPropertySetupModel.setStrAdvAmtSMSContent(objBean.getStrAdvAmtSMSContent());
		objPropertySetupModel.setStrCheckOutSMSContent(objBean.getStrCheckOutSMSContent());
		objPropertySetupModel.setStrSMSAPI(objBean.getStrSMSAPI());
		return objPropertySetupModel;
    }    
}
