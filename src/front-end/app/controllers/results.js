import Ember from 'ember';

export default Ember.Controller.extend({

    /**
     * Returns the path of the current statistics for sharing purposes
     */
    currentPath: function(){
        // http://127.0.0.1:4200/results/572c8c90ce73f23439e6b206/ladder
        return window.location.href.split("/").slice(0,5).join("/");
    }.property('currentPath')
});
