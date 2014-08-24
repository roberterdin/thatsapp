App.ResultsIndexController = Ember.Controller.extend({
    needs: "results",
    parentController : Ember.computed.alias('controllers.results'),

    init : function(){
        console.log("ResultsIndexController created");
        console.log(this.get('parentController').get('model'));
    }
});