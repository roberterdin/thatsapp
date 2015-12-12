import DS from 'ember-data';

export default DS.JSONSerializer.extend(DS.EmbeddedRecordsMixin, {
  attrs: {
    aggregatedHistory: { embedded: 'always' }
  },
  normalizePayload: function(payload) {
    delete payload._type;
    delete payload.className;
    delete payload._embedded;

    payload.conversation = payload.conversation.$id.$oid;
    payload.statistics = payload.statistics.$id.$oid;

    for(var i = 0; i < payload.participants.length; i++) {
      payload.participants[i] = payload.participants[i].$id.$oid;
    }

    for(var i = 0; i < payload.aggregatedHistory.length; i++) {
      payload.aggregatedHistory[i].id = payload.aggregatedHistory[i]._id.$oid;
      payload.aggregatedHistory[i].statistics.id = payload.aggregatedHistory[i]._id.$oid;
    }


    payload.id = payload._id.$oid;
    delete payload._id.$oid;

    console.log(payload);
    return payload;
  }
});
