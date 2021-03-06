import DS from 'ember-data';

export default DS.JSONSerializer.extend(DS.EmbeddedRecordsMixin, {
  attrs: {
  statistics: { embedded: 'always' }
  },
  normalizePayload: function(payload) {
    delete payload._type;
    delete payload.className;
    delete payload._embedded;

    payload.id = payload._id.$oid;
    delete payload._id.$oid;

    return payload;
  }
});
