import DS from 'ember-data';
/**
 * requires moment.js for formatting
 * @type {void|*}
 */
 /* global moment */
export default DS.Model.extend({
    date: DS.attr('date'),
    sender: DS.attr('string'),
    content: DS.attr('string'),

    strDate : function(){
        return moment(this.get('date')).format('DD. MMM YYYY');
    }.property('date'),

    strTime : function(){
        return moment(this.get('date')).format('mm:HH');
    }.property('date')
});