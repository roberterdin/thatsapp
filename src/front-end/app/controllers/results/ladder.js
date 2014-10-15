import Ember from 'ember';

export default Ember.Controller.extend({

    needs: "results",
    parentController : Ember.computed.alias('controllers.results'),

    init : function(){
        console.log("ResultsLadderController created");
    },

	messagesChart : function(){
		var that = this;
		var chartData = [];
		
		this.get("parentController").get("senders").forEach(function(value,key){
			var entry = {};
			entry.name = value.name;
			entry.data = [value.messageAmount];
			chartData.push(entry);
		});

		chartData.sort(function (a, b) {
    		if (a.data[0] > b.data[0]) {
      			return 1;
      		}
      		if (a.data[0] < b.data[0]){
				return -1;
			}
			return 0;
		});

		console.log(chartData);


		Ember.$(function () {
		    Ember.$('#chart-ladder-messages').highcharts({
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
		        series: chartData
		    });
		});

	}

});

