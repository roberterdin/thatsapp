import DS from 'ember-data';

export default DS.Model.extend({

  conversation: DS.belongsTo('conversation'),
  participants: DS.hasMany('person'),
  statistics: DS.belongsTo('statistics')

});
