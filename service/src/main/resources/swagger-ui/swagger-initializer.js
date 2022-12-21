window.onload = function() {
  //<editor-fold desc="Changeable Configuration Block">

  current_url = window.location.href;
  api_docs_url = current_url.replace('/swagger-ui/index.html', '/api-docs');
  // the following lines will be replaced by docker/configurator, when it runs in a docker-container
  window.ui = SwaggerUIBundle({
    url: api_docs_url,
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout"
  });

  //</editor-fold>
};
