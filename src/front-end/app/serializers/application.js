import DS from 'ember-data';

export default DS.JSONSerializer.extend({
  normalizePayload: function(payload) {
    console.log("Application: Normalising payload of JSON");
    delete payload._type;
    delete payload.className;
    delete payload._embedded;

    payload.id = payload._id.$oid;
    delete payload._id.$oid;

    return payload;
  }
});
