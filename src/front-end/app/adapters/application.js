import DS from 'ember-data';

export default DS.RESTAdapter.extend({
  // document.location.origin --> "http://localhost:4200"
  host: document.location.origin.split(/:\d*$/g, 1)[0] + ":8080", // --> "http://localhost:8080
  namespace: 'whatistics'
});
