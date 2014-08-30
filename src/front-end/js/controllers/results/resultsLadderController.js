App.ResultsLadderController = Ember.Controller.extend({
    needs: "results",
    parentController : Ember.computed.alias('controllers.results'),

    init : function(){
        console.log("ResultsLadderController created");
    },

	messagesChart : function(){
		var that = this;

		$(function () {
		    $('#chart-ladder-messages').highcharts({
		        chart: {
		            type: 'bar'
		        },
		        title: {
		            text: ''
		        },
		        legend: {
		            enabled: false
		        },
		        xAxis: {
		            labels: {
		                enabled: false
		            }
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: '',
		                align: 'high'
		            },
		            labels: {
		                overflow: 'justify'
		            }
		        },
		        plotOptions: {
		            bar: {
		                dataLabels: {
		                    enabled: true,
		                    format: '{series.name}',
		                    inside: true,
		                    color: '#FFFFFF',
		                    align: 'left',
		                    x: 5
		                }
		            },
		        	series: {
		        		groupPadding: 0
		        	}
		        },
		        credits: {
		            enabled: false
		        },
		        tooltip: {
		            headerFormat: '',
		            valueSuffix: ' messages sent'
		        },
		        exporting: {
		        	enabled: false
		        },
		        series: [
		            {
		                name: 'Robi',
		                data: [112]
		            },
		            {
		                name: 'Sibi',
		                data: [245]
		            },
		            {
		                name: 'Nita',
		                data: [345]
		            },
		            {
		                name: 'Michi',
		                data: [400]
		            }
		        ]
		    });
		});

	}

});
