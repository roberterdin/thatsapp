import DS from 'ember-data';

export default DS.Model.extend({

  conversation: DS.belongsTo('conversation'),
  participants: DS.hasMany('person', {async: true}),
  statistics: DS.belongsTo('statistics', {async: true})

});
