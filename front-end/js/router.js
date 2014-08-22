App.Router.map(function() {
    this.resource('index', { path: '/' }, function () {

    });


});

App.IndexRoute = Ember.Route.extend({
    model: function() {
        return ['red', 'yellow', 'blue'];
    }
});
