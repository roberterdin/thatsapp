import Ember from 'ember';

export default Ember.View.extend({
	templateName: 'results/index',

    didInsertElement : function(){
        this._super();
        //this.get('controller').historyChart();
    }
});
