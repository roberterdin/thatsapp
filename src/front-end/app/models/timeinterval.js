import DS from 'ember-data';
/**
 * @type {void|*}
 */
export default DS.Model.extend({
  startInstant: DS.attr('date'),
  endInstant: DS.attr('date'),
  label: DS.attr('string'),
  statistics: DS.belongsTo('statistics', {async: false})
});
