App.Router.map(function() {
    this.resource('index', { path: '/' }, function () {

    });

    this.resource('results', {path: '/results'}, function(){

    });


});

App.IndexRoute = Ember.Route.extend({
    model: function() {
        return ['red', 'yellow', 'blue'];
    }
});
