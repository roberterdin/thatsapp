import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
  this.resource('results', {path: '/results'}, function(){
        this.resource('results.ladder', {path: "/ladder"}, function(){
        });
        this.resource('results.individual', {path: "/individual"}, function(){
        });
    });
});

export default Router;


