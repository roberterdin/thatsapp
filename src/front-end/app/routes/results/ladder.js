import Ember from 'ember';

export default Ember.Route.extend({
    model() {

        // preload stuff because it needs to be available right away to create the charts
        //return this.store.find('globalstatistics', this.paramsFor('results').statistics_id).then(globalStatistics => {
        //    return globalStatistics.get('participants').then(participants => {
        //        var statisticsPromises = participants.getEach('statistics');
        //        return Ember.RSVP.all(statisticsPromises).then(() => {
        //           return this.store.find('globalstatistics', this.paramsFor('results').statistics_id);
        //        });
        //    });
        //});

        return this.store.find('globalstatistics', this.paramsFor('results').statistics_id).then(globalStatistics => {
            return globalStatistics.get('statistics').then(() =>{
                return globalStatistics.get('participants').then(participants => {
                    var statisticsPromises = participants.getEach('statistics');
                    return Ember.RSVP.all(statisticsPromises).then(() => {
                        return this.store.find('globalstatistics', this.paramsFor('results').statistics_id);
                    });
                });
            });
        });
    }
});
