import DS from 'ember-data';

export default DS.Model.extend({
  wordAmount: DS.attr('number'),
  messageAmount: DS.attr('number'),
  mediaAmount: DS.attr('number'),

  vocabulary: DS.attr(),
  emoticons: DS.attr(),

  globalStatistics: DS.belongsTo('globalstatistics')
});
