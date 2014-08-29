App.ResultsIndexView = Ember.View.extend({
    templateName: 'results.index',

    didInsertElement : function(){
        this._super();
        Ember.run.scheduleOnce('afterRender', this, function(){
            console.log("ResultsIndexView rendered");
            this.get('controller').historyChart();
        });
    }
});