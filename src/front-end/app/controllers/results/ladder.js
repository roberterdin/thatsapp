import Ember from 'ember';

export default Ember.Controller.extend({

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
            labels.push(participant.get('name'));
            messageAmount.push(participant.get('statistics.messageAmount'));
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
    }.property('messageChart'),

    mediaChart: function() {
        var labels = [];
        var mediaAmount = [];

        var sortedSlicedParticipants = this.get('model.participants').toArray().sort(function(a, b){
            if (a.get('statistics').get('mediaAmount') < b.get('statistics').get('mediaAmount')){
                return 1;
            }
            if ( a.get('statistics').get('mediaAmount') > b.get('statistics').get('mediaAmount' )){
                return -1;
            }
            return 0;
        }).slice(0,9);

        sortedSlicedParticipants.forEach(function(participant) {
            labels.push(participant.get('name'));
            mediaAmount.push(participant.get('statistics.mediaAmount'));
        });

        return {
            chartData: {
                labels: labels,
                series: [
                    mediaAmount
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
    }.property('mediaChart'),

    emoticons: function(){

        return $.map(this.get('model.statistics.emoticons'), (k,v) => {
            return {emoji: v, amount: k};
        }).sort((a, b) => {
            if (a.amount < b.amount){
                return 1;
            }
            if ( a.amount > b.amount){
                return -1;
            }
            return 0;
        }).slice(0,9);

    }.property('emoticons')
});

