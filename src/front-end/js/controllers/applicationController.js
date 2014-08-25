App.ApplicationController = Ember.Controller.extend({



    year: function(){
        return new Date().getFullYear();
    }.property()

});