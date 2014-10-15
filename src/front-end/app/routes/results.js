import Ember from 'ember';

export default Ember.Route.extend({
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
