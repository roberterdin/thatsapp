App.Router.map(function() {
    this.resource('index', { path: '/' }, function () {

    });

    this.resource('results', {path: '/results'}, function(){
        this.resource('results.ladder', {path: "/ladder"}, function(){
        });
        this.resource('results.individual', {path: "/individual"}, function(){
        });
    });


});

App.IndexRoute = Ember.Route.extend({
    model: function() {
        return ['red', 'yellow', 'blue'];
    },
    setupController: function(controller, model) {
        this._super(controller, model);
        controller._reset();
    }
});

App.ResultsRoute = Ember.Route.extend({
    model: function(){
        // LOCAL call only!
        return {messages : this.store.all('message').toArray()};
    },
    setupController: function(controller, model){
        console.log('start setup');
        this._super(controller, model);
        controller._generateStatistics();
    }
});

App.ResultsIndexRoute = Ember.Route.extend({

});