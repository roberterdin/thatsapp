import Ember from 'ember';

export default Ember.Route.extend({
    afterModel() {
        Ember.run.later((function() {
            history.back();
        }), 8000);
    }
});
