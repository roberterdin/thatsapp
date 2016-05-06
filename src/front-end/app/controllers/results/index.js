import Ember from 'ember';

export default Ember.Controller.extend({

    needs: "results",
    parentController: Ember.computed.alias('controllers.results'),

    init: function() {
        console.log("ResultsIndexController created");
    },

    historyChart: function() {
        var labels = [];
        var messageAmount = [];
        this.get('model.aggregatedHistory').forEach(function(timeInterval) {
            labels.push(timeInterval.get('label'));
            messageAmount.push(timeInterval.get('statistics').get('messageAmount'));
        });
        return {
            chartData: {
                labels: labels,
                series: [
                    messageAmount
                ]
            },
            chartOptions: {
                showPoint: false,
                lineSmooth: false,
            }
        };
    }.property('historyChart')


});
