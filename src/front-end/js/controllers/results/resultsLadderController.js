App.ResultsLadderController = Ember.Controller.extend({
    needs: "results",
    parentController : Ember.computed.alias('controllers.results'),

    init : function(){
        console.log("ResultsLadderController created");
    }

});