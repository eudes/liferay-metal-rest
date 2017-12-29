package es.eudes.metalrest.ws;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import java.util.Collections;
import java.util.Set;

import org.osgi.service.component.annotations.Component;


@Component(immediate = true, service = Application.class)
@ApplicationPath("/portlet")
public class MetalRestResources extends Application {

	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@Path("/hello")
	@Produces("text/text")
	public String sayHello() {

		return "Server says Hello!";
	}
}