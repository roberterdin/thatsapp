import DS from 'ember-data';
/**
 * @type {void|*}
 */
export default DS.Model.extend({
  name: DS.attr('string'),
  message: DS.belongsTo('message')
});
