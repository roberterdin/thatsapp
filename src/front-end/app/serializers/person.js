import DS from 'ember-data';

export default DS.JSONSerializer.extend({

    normalizeResponse(store, primaryModelClass, payload, id, requestType) {
        delete payload._type;
        delete payload.className;
        delete payload._embedded;

        payload.statistics = payload.statistics.$id.$oid;

        payload.id = payload._id.$oid;
        delete payload._id.$oid;

        if(payload.name !== "_dummy"){
            payload.isSysMsg = false;
        }else {
            payload.isSysMsg = true;
        }


        return this._super(...arguments);
    }

});
