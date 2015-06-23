import DS from 'ember-data';

export default DS.JSONSerializer.extend(DS.EmbeddedRecordsMixin, {
  attrs: {
    messages: { embedded: 'always' }
  },
  normalizePayload: function(payload) {
    delete payload._type;
    delete payload.className;
    delete payload._embedded;
    payload.id = payload._id.$oid;

    payload.messages.forEach(function(message){
      message.id = message._id.$oid;
      message.sender.id = message.sender._id.$oid;

      message.sendDate = message.sendDate.$date;
      delete message.sendDate.$date;
    });

    console.log(payload);
    return payload;
  }
});
