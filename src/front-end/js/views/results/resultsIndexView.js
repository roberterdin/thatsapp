App.ResultsIndexView = Ember.View.extend({
    templateName: 'results.index',

    didInsertElement : function(){
        this._super();
        console.log("ResultsIndexView rendered");
        this.get('controller').historyChart();
    }
});