import Ember from 'ember';

export default Ember.Controller.extend({

    parentController: Ember.computed.alias('controllers.results'),

    init: function() {
        console.log("ResultsLadderController created");
    },

    messageChart: function() {
        var labels = [];
        var messageAmount = [];

        var sortedSlicedParticipants = this.get('model.participants').toArray().sort(function(a, b){
            if (a.get('statistics').get('messageAmount') < b.get('statistics').get('messageAmount')){
                return 1;
            }
            if ( a.get('statistics').get('messageAmount') > b.get('statistics').get('messageAmount' )){
                return -1;
            }
            return 0;
        }).slice(0,9);

        sortedSlicedParticipants.forEach(function(participant) {
            console.log(participant.get('name'));
            labels.push(participant.get('name'));
            messageAmount.push(participant.get('statistics.messageAmount'));
            console.log(participant.get('statistics.messageAmount'));
        });

        return {
            chartData: {
                labels: labels,
                series: [
                    messageAmount
                ]
            },
            chartOptions: {
                seriesBarDistance: 10,
                reverseData: true,
                horizontalBars: true,
                axisY: {
                    offset: 70
                }
            }
        };
    }.property('messageChart')

});
