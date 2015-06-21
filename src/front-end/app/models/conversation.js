import DS from 'ember-data';

export default DS.Model.extend({
  submittedBy: DS.attr('string'),

  messages: DS.hasMany('message')

});
