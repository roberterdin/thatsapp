import Ember from 'ember';

export default Ember.View.extend({
	templateName: 'results/ladder',

    didInsertElement : function(){
        this._super();
        console.log("ResultsLadderView rendered");
        this.get('controller').messagesChart();
    }
});
