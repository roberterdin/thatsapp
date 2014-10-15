import Ember from 'ember';

export default Ember.Controller.extend({
    needs: "results",
    parentController : Ember.computed.alias('controllers.results'),

    init : function(){
        console.log("ResultsIndividualController created");
    }

});
