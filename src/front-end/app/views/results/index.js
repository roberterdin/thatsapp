import Ember from 'ember';

export default Ember.View.extend({
	templateName: 'results/index',

    didInsertElement : function(){
        this._super();
        console.log("ResultsIndexView rendered");
        //this.get('controller').historyChart();
    }
});
