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

    for(let i = 0; i < payload.participants.length; i++) {
      payload.participants[i] = { type: "person", id: payload.participants[i].$id.$oid };
    }

    for(let i = 0; i < payload.aggregatedHistory.length; i++) {
      payload.aggregatedHistory[i].id = payload.aggregatedHistory[i]._id.$oid;
      payload.aggregatedHistory[i].statistics.id = payload.aggregatedHistory[i]._id.$oid;
      payload.aggregatedHistory[i].startInstant = payload.aggregatedHistory[i].startInstant.$date;
      payload.aggregatedHistory[i].endInstant = payload.aggregatedHistory[i].endInstant.$date;
    }


    payload.id = payload._id.$oid;
    delete payload._id.$oid;

    console.log(payload);
    return payload;
  }
});
