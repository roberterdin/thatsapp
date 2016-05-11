import Ember from 'ember';

export default Ember.Controller.extend({


    currentParticipant: null,


    actions: {
        selectParticipant: function(participantId){
            this.set('currentParticipant',this.store.peekRecord('person', participantId));
        }
    }

});

