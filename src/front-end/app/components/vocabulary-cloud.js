/* global d3 */
import Ember from 'ember';
import $ from 'jquery';
// import cloud from 'cloud';


export default Ember.Component.extend({


    didInsertElement() {
        this._super(...arguments);

        window.vocabFill = d3.scale.category20();
        let vocab = [];
        this.get('normalizedVocabulary').forEach( (e) => {
            vocab.push({text: e.text, size: e.size});
        })

        console.log(this.draw);
        window.vocabLayout = d3.layout.cloud()
        window.vocabLayout
            .size([800, 500])
            .words(vocab)
            .padding(5)
            .rotate(function() { return ~~(Math.random() * 2) * 90; })
            .font("Impact")
            .fontSize(function(d) { return d.size; })
            .on("end", this.draw);

        window.vocabLayout.start();
    },

    wordAmount: 100,

    vocabulary: Ember.computed('model', function() {
        let result = $.map(this.get('model'), (k, v) => {
            return {text: v, size: k};
        }).sort((a, b) => {
            if(a.size < b.size) {
                return 1;
            }
            if(a.size > b.size) {
                return -1;
            }
            return 0;
        }).slice(0, this.get('wordAmount'));

        return result;
    }),

    normalizedVocabulary: Ember.computed('vocabulary', function() {

        let max = this.get('vocabulary')[0].size;
        let min = this.get('vocabulary')[this.get('wordAmount') - 1].size;
        let max_ = 1000; // max' new highest value
        let min_ = 1; // min' new lowest value

        // linear transformation constants
        // (max' - min') / (max - min)
        let a = (max_ - min_) / (max - min);
        // max' - a * max
        let b = max_ - a * max;

        let result = [];
        this.get('vocabulary').forEach((word) => {
            result.push({text: word.text, size: Math.round(a * word.size + b)});
        });

        return result;
    }),

    limitedVocabulary: Ember.computed('vocabulary', function() {
       return this.get('vocabulary').slice(0,20);
    }),

    draw(words) {
        console.log("draw called");
        console.log(window.vocabLayout);
        d3.select("#vocabCloud").append("svg")
            .attr("width", window.vocabLayout.size()[0])
            .attr("height", window.vocabLayout.size()[1])
            .append("g")
            .attr("transform", "translate(" + window.vocabLayout.size()[0] / 2 + "," + window.vocabLayout.size()[1] / 2 + ")")
            .selectAll("text")
            .data(words)
            .enter().append("text")
            .style("font-size", function(d) {
                return d.size + "px";
            })
            .style("font-family", "Impact")
            .style("fill", function(d, i) {
                return window.vocabFill(i);
            })
            .attr("text-anchor", "middle")
            .attr("transform", function(d) {
                return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
            })
            .text(function(d) {
                return d.text;
            });
    }
});
