import Ember from 'ember';

export default Ember.Controller.extend({
  init: function() {
    this._super();
    console.log("Results controller created");
  },


  _reset: function() {
    console.log("resetting ResultsController");
  },

  // TODO: check if still used
  getStartDate: function() {
    return this.get('model.messages').get('firstObject').get('sendDate');
  },

  _generateStatistics: function() {
    this._reset();
  }
});
