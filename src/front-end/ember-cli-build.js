var EmberApp = require('ember-cli/lib/broccoli/ember-app');

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    // Any other options
  });

  app.import('bower_components/bootstrap/dist/js/bootstrap.js');
  app.import('bower_components/bootstrap/dist/css/bootstrap.css');

  app.import('bower_components/bootstrap/dist/fonts/glyphicons-halflings-regular.woff', {
    destDir: 'fonts'
  });
  app.import('bower_components/bootstrap/dist/css/bootstrap.css.map', {
    destDir: 'assets'
  });

  //app.import('bower_components/moment/moment.js');
  //app.import('bower_components/modernizr/modernizr.js');

  //app.import('bower_components/es6-shim/es6-shim.js');


  /* vendors */
  //app.import('vendor/bootstrap.file-input.js');
  //app.import('vendor/file-reader.js');
  //app.import('vendor/wordCounter.js');
  //app.import('vendor/stopwords_de_ch.js');
  //app.import('vendor/stopwords_de_de.js');
  //app.import('vendor/vocabBuilder.js');

  return app.toTree();
};
