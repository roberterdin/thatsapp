import DS from 'ember-data';

export default DS.JSONSerializer.extend(DS.EmbeddedRecordsMixin, {
    attrs: {
        messages: {embedded: 'always'}
    },
    normalizeResponse(store, primaryModelClass, payload, id, requestType) {
        delete payload._type;
        delete payload.className;
        delete payload._embedded;
        payload.id = payload._id.$oid;

        payload.messages.forEach(function(message) {
            message.id = message._id.$oid;
            message.sender.id = message.sender._id.$oid;

            message.sendDate = message.sendDate.$date;
            delete message.sendDate.$date;
        });


        return this._super(...arguments);
    }
});
