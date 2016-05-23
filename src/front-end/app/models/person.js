import DS from 'ember-data';
/**
 * @type {void|*}
 */
export default DS.Model.extend({
    name: DS.attr('string'),
    statistics: DS.belongsTo('statistics', {async: true}),
    isSysMsg : DS.attr('boolean')
});
