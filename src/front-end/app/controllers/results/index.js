import Ember from 'ember';

export default Ember.Controller.extend({

  needs: "results",
  parentController: Ember.computed.alias('controllers.results'),

  init: function() {
    console.log("ResultsIndexController created");
  },

  // TODO only dummy data inside
  historyChart: function () {
    return {
      chartData: {
        labels: ['Day1', 'Day2', 'Day3'],
        series: [
          [5, 4, 8],
          [10, 2, 7],
          [8, 3, 6]
        ]
      }
    }
  }.property('historyChart'),


});
