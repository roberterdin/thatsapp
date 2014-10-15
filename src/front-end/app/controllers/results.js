import Ember from 'ember';
/* global moment, countWords */
/* global vocabBuilder */

/**
 * @class
 * @param name
 * @constructor
 */
function Sender(name){
    this.name = name;
    this.messageAmount = 0;
    this.wordAmount = 0;
    this.mediaAmount = 0;
    this.vocabulary = new Map();
}

/**
 * @class
 * @constructor
 */
function GlobalStat(){
    this.senderAmount = 0; //TODO: redundant information with senders.size()
    this.messageAmount = 0;
    this.wordAmount = 0;
    this.mediaAmount = 0;
    this.vocabulary = new Map();
}

export default Ember.ObjectController.extend({
     init : function(){
        this._super();
        console.log("Results controller created");
        console.log(this);
    },


    _reset : function(){
        console.log("resetting ResultsController");
        this.globalStat = new GlobalStat();
        this.senders = new Map();
        this.history = new Map();
    },


    getStartDate : function(){
        return  this.get('model.messages')[0].get('date');
    },

    _generateStatistics : function(){

        this._reset();


        // precompile regEx
        // TODO: extract for easy maintainability
        var mediaPattern = /(<Media omitted>|<Mediendatei entfernt>|<Medien weggelassen>)/;


        var that = this;
        this.get('model.messages').forEach(function(message){

            // create sender if not already existing
            if(that.get('senders').get(message.get('sender')) === undefined){
                that.get('senders').set(message.get('sender'), new Sender(message.get('sender')));
                that.get('globalStat').senderAmount++;
            }


            that.get('globalStat').messageAmount++;
            that.get('senders').get(message.get('sender')).messageAmount++;


            if(mediaPattern.test( message.get('content') )){ // message was some sort of media data

                that.get('globalStat').mediaAmount++;
                that.get('senders').get(message.get('sender')).mediaAmount++;

            }else{// regular message

                // adjust word count
                var tmpWordCount = countWords(message.get('content'));
                that.get('globalStat').wordAmount += tmpWordCount;
                that.get('senders').get(message.get('sender')).wordAmount += tmpWordCount;

                // build vocabulary
                vocabBuilder.build(message.get('content'), that.get('globalStat').vocabulary, that.get('senders').get(message.get('sender')).vocabulary);
            }


            // generate history
            var date = moment(message.get('date'));
            if(that.get('history').has(date.format('DD.MM.YY')) === false){
                that.get('history').set(date.format('DD.MM.YY'), 1);
            }else{
                that.get('history').set(date.format('DD.MM.YY'), that.get('history').get(date.format('DD.MM.YY')) + 1);
            }
        });

        // bring things in order
        console.log(this.globalStat.vocabulary.entries());

    }
});