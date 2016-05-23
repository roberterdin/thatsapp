import Ember from 'ember';

export default Ember.Controller.extend({

    currentParticipant: null,
    currentEmoticons: Ember.computed('currentParticipant', function(){
        return this.get('currentParticipant.statistics.emoticons');
    }),

    actions: {
        selectParticipant: function(participantId){
            this.set('currentParticipant', this.store.peekRecord('person', participantId));
        }
    }
});
