App.ResultsIndexController = Ember.Controller.extend({
    needs: "results",
    parentController : Ember.computed.alias('controllers.results'),

    init : function(){
        console.log("ResultsIndexController created");
    },


    historyChart : function(){
        var that = this;
        var dateSeries = [];

        this.get('parentController').get('history').forEach(function(value, key){
            dateSeries.push(value);
        });


        // TODO: implement grouping into n groups
        $(function () {
            $('#history-chart').highcharts({
                chart: {
                    zoomType: 'x',
                    type: 'areaspline'
                },
                title: {
                    text: 'Messages per Day',
                    style: {
                        display: 'none'
                    }
                },
                subtitle: {
                    text: document.ontouchstart === undefined ?
                        'Click and drag in the plot area to zoom in' :
                        'Pinch the chart to zoom in'
                },
                xAxis: {
                    type: 'datetime',
                    minRange: 1 * 24 * 3600 * 1000 // every day
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Messages'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    areaspline: {
                        fillColor: {
                            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                            stops: [
                                [0, Highcharts.getOptions().colors[0]],
                                [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                            ]
                        },
                        marker: {
                            enabled: false,
                            radius: 2
                        },
                        lineWidth: 1,
                        states: {
                            hover: {
                                lineWidth: 1
                            }
                        },
                        threshold: null
                    }
                },

                series: [{
                    type: 'areaspline',
                    name: 'Messages per Day',
                    pointStart: Date.UTC(that.get('parentController').getStartDate().getYear(),
                        that.get('parentController').getStartDate().getMonth(),
                        that.get('parentController').getStartDate().getDay()),
                    pointInterval: 24 * 3600 * 1000,
                    data: dateSeries
                }],

                exporting: {
                    filename: "Whatistics-Timeline",
                    scale: 3
                }
            });
        });

    }




});