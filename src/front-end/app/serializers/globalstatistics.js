import DS from 'ember-data';

export default DS.JSONSerializer.extend({
  normalizePayload: function(payload) {
    delete payload._type;
    delete payload.className;
    delete payload._embedded;

    payload.conversation = payload.conversation.$id.$oid;
    payload.statistics = payload.statistics.$id.$oid;

    for(var i = 0; i < payload.participants.length; i++) {
      payload.participants[i] = payload.participants[i].$id.$oid;
    }

    payload.id = payload._id.$oid;
    delete payload._id.$oid;

    return payload;
  }
});
