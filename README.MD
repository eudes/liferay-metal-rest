# Sample MetalJS and Rest portlet

This is a sample portlet aimed to illustrate how to configure a portlet to allow use of 
MetalJS with a Rest API to retrieve resources. On top of that, I've chosen JSX instead of 
the standard Soy template language.

## Structure overview
```
metal-rest-portlet
|-bnd.bnd (OSGi)
|-build.gradle (Java)
// JS
|-.babelrc Babel (transpiler) config
|-.npmbundlerrc NPM bundler config
|-package.json JS package meta data
// Sources
|- src/main
    |-java/es.eudes.metalrest
        |- portlet
            |- MetalRestPortlet.java
                MVCPortlet definition (like any other MVCPortlet)
        |- ws
            |-MetalRestResources.java
                Web service definition
    |-resources
        |- configuration
            |- com.liferay.portal.remote.cxf.common.configuration.CXFEndpointPublisherConfiguration-cxf.properties
                CXF Endpoint definition
            |- com.liferay.portal.remote.rest.extender.configuration.RestExtenderConfiguration-rest.properties
                Rest Extender definition
        |-META-INF.resources
            |- lib
                |-index.es.js
                    Main JS file. Imports modules and inits the portlet
                |-Modal.js
                    Modal Module (class)
        |- view.jsp, init.jsp
            Standard JSPs
```
## Portlet
The portlet is a basic MVCPortlet without any special details.

```Java
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

```

The only thing worth of note is the "/view.jsp" entrypoint. This is where we will import 
(require) our JS module.

```JSP
<aui:script require="metal-rest@1.0.0" >
    metalRest100.default('<portlet:namespace />-button');
</aui:script>
```

## MetalJS / JSX
I've chosen JSX as the template language because I wanted to focus on JS rendering and Soy
is no longer being mantained. This has the downside that there's no compatibility between the
JS and Java HTML rendering. You have to use JSP for the Java side, and JSX for the JS side.
If your are interested in server side rendering (Isomorphic code), take a look at 
[https://metaljs.com/docs/guides/isomorphic.html#server_rendering]. This shifts the load of
rendering the components to the server, instead of the client.

To enable MetalJS with JSX in the portlet we need to create and configure a few files. 

### package.json
This defines the metadata, dependencies and build commands. For metal + JSX you need:

```json
{
  "dependencies": {
    "metal-component": "^2.15.0",
    "metal-incremental-dom": "^2.15.1",
    "metal-jsx": "^2.15.1",
  },
  "devDependencies": {
    "babel-cli": "^6.26.0",
    "babel-preset-es2015": "^6.24.1",
    "babel-preset-liferay-project": "^1.3.0",
    "babel-preset-metal": "^4.1.0",
    "babel-preset-metal-jsx": "^0.1.1",
    "liferay-npm-bundler": "^1.3.0",
    "liferay-npm-bundler-preset-standard": "^1.3.0"
  },
  "main": "lib/index.es.js",
  "name": "metal-rest",
  "private": true,
  "scripts": {
    "build": "babel --source-maps -d build/resources/main/META-INF/resources src/main/resources/META-INF/resources && liferay-npm-bundler"
  },
  "version": "1.0.0"
}
```

### .npmbunlderrc: 
[https://dev.liferay.com/hu/develop/tutorials/-/knowledge_base/7-0/configuring-liferay-npm-bundler]
Bundler presets: Use the standard preset ("liferay-npm-bundler-preset-standard")

### .babelrc:
[https://babeljs.io/docs/usage/babelrc/]
Transpiler presets:
- es2015: to allow modern JS style
- liferay-project: bindings for liferay
- metal-jsx: jsx support

### src/resources/META-INF/resources/lib/index.es.js
This is the JS entrypoint. This is the module that gets exported under the name "metal-rest"
defined in the bnd.bnd Web-ContextPath property.

### bnd.bnd
Adding the Web-ContextPath property makes our JS available to download through a static URL:
/o/metal-rest. This way, we can require it through ``<aui:script require="metal-rest>``, 
or through the Liferay JS module Loader.

### src/resources/META-INF/resources/view.jsp
Here we use our module (through the ``<aui:script>`` taglib).

## REST API
Liferay allows the publication of REST and SOAP Web Services with [Apache CXF](http://cxf.apache.org).
To create a CXF Endpoint we can either configure it manually, o do it programmatically.
I've chosen the second approach. For this, we just need to add some configuration files.

### WS Config
We can add config files by:
- Adding the Liferay-Configuration-Path to our bnd.bnd
- Adding the files to the defined path (src/main/resources/configuration)

#### CXF Endpoint
com.liferay.portal.remote.cxf.common.configuration.CXFEndpointPublisherConfiguration-cxf.properties

This file defines the new endpoint. We can then publish services to this endpoint. They will
be accessible through the URL we have defined in the ``contextPath`` property (/o/metalrest).

#### REST Extender
com.liferay.portal.remote.rest.extender.configuration.RestExtenderConfiguration-rest.properties

In this file we choose to which services this config will be applied (``jaxRsServiceFilterStrings``)
and add them to the endpoint (``contextPaths``). We can add more than one to both properties.

### Our Web Service
To define the webservice itself, we just need to create a CXF Application, as illustrated
in ``MetalRestResources.java``.

This service will be accessible through the endpoint and service URL: 
``/o/[endpoint]/[applicationPath]/[method]``.
In our case, the ``hello`` method will live under ``/o/metalrest/portlet/hello``.

### JAVA Dependencies
The only special dependency to allow REST applications in our portlet is:
    
```
compileOnly group: 'javax.ws.rs', name: 'javax.ws.rs-api', version: '2.0'
```

### A note about Liferay gradle plugin versions
For the gradle-NPM integration to work, your workspace or project must be configured to use
the following versions of the gradle plugins. This config can be added to the project's 
gradle.build, or the the Liferay workspace settings.gradle buildscript dependencies.

```
buildscript {
    dependencies{
        classpath group: "com.liferay", name: "com.liferay.gradle.plugins.workspace", version: "1.8.0"
        classpath group: "com.liferay", name: "com.liferay.gradle.plugins.node", version: "4.0.0"
    }
}
```
