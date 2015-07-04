import Ember from 'ember';

export default Ember.Route.extend({
	model: function(params){

        // LOCAL call only!
        //return {messages : this.store.all('message').toArray()};
        return this.store.find('globalstatistics', params.statistics_id);
    },
    setupController: function(controller, model){
        this._super(controller, model);
        controller._generateStatistics();
    }
});
