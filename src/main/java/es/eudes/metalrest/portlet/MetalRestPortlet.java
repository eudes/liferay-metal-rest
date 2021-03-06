package es.eudes.metalrest.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
		property = {
				"com.liferay.portlet.display-category=category.sample",
				"com.liferay.portlet.instanceable=true",
				"javax.portlet.display-name=Metal Rest",
				"javax.portlet.init-param.template-path=/",
				"javax.portlet.init-param.view-template=/view.jsp",
				"javax.portlet.security-role-ref=power-user,user"
		},
		service = Portlet.class
)
public class MetalRestPortlet extends MVCPortlet {
}
