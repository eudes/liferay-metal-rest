<%@ include file="/init.jsp" %>

<button id="<portlet:namespace />-button">
    Click me to open a superb modal dialog!
</button>

<aui:script require="metal-rest@1.0.0" >
    // Loads the metal-rest module, 1.0.0 version as "metalRest100"
    metalRest100.default('<portlet:namespace />-button');
</aui:script>