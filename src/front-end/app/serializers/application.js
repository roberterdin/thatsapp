import JSONSerializer from 'ember-data/serializers/json';


export default JSONSerializer.extend({
    normalizeResponse(store, primaryModelClass, payload, id, requestType) {

        delete payload._type;
        delete payload.className;
        delete payload._embedded;
        delete payload._links;

        payload.id = payload._id.$oid;
        delete payload._id;

        return this._super(...arguments);
    }
});
