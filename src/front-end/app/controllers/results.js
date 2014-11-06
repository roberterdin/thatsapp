import Ember from 'ember';
/* global moment, countWords */
/* global vocabBuilder */
/* global XRegExp */

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
    this.vocabulary = [];
    this.emojis = [];
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
    this.vocabulary = [];
    this.emojis = [];
}

function vocabComparator ( a, b ){
    if(a.amount < b.amount){
        return 1;
    }else if( a.amount > b.amount ){
        return -1;
    }else if( a.amount === b.amount){
        return 0;
    }else{
        console.error("Comparison of vocabulary objects failed");
    }
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

        // temp variables
        var tmpGlobalVocab = new Map();
        var tmpGlobalEmoji = new Map();
        var tmpSenders = new Map(); // store temporary, sender-specific data for processing purposes


        var that = this;
        this.get('model.messages').forEach(function(message){

            // create sender if not already existing
            if(that.get('senders').get(message.get('sender')) === undefined){
                that.get('senders').set(message.get('sender'), new Sender(message.get('sender')));
                that.get('globalStat').senderAmount++;
            }

            // create temporary sender
            if(tmpSenders.get(message.get('sender')) === undefined){
                tmpSenders.set(message.get('sender'), {
                    vocabulary : new Map(),
                    emoji: new Map()
                });
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
                vocabBuilder.build(message.get('content'), tmpGlobalVocab, tmpSenders.get(message.get('sender')).vocabulary);

                // get emojis
                var emojiRegEx = XRegExp('[\uD800-\uDBFF][\uDC00-\uDFFF]','g');
                XRegExp.forEach(message.get('content'), emojiRegEx, function(match){
                    // global
                    if(tmpGlobalEmoji.get(match[0]) === undefined){
                        tmpGlobalEmoji.set(match[0], 0);
                    }
                    tmpGlobalEmoji.set(match[0], tmpGlobalEmoji.get(match[0]) + 1);

                    // sender
                    if(tmpSenders.get(message.get('sender')).emoji.get(match[0]) === undefined){
                        tmpSenders.get(message.get('sender')).emoji.set(match[0], 0);
                    }
                    tmpSenders.get(message.get('sender')).emoji.set(match[0], tmpSenders.get(message.get('sender')).emoji.get(match[0]) + 1);
                });
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
        // -----

        // vocabulary

        tmpGlobalVocab.forEach( function(value, key){
            that.globalStat.vocabulary.push({
                word: key,
                amount: value
            });
        });
        this.globalStat.vocabulary.sort( vocabComparator );

        tmpSenders.forEach( function( value, key ){
            var sender = key;
            value.vocabulary.forEach( function( value, key ){
                that.senders.get(sender).vocabulary.push({
                    word: key,
                    amount: value
                });
            });
            that.senders.get(sender).vocabulary.sort( vocabComparator );
        });

        // emojis

        tmpGlobalEmoji.forEach( function (value, key){
            that.globalStat.emojis.push({
                    emoji: key,
                    amount: value
                });
        });
        that.globalStat.emojis.sort( vocabComparator );

        tmpSenders.forEach( function( value, key){
           var sender = key;
            value.emoji.forEach( function( value, key){
               that.senders.get(sender).emojis.push({
                   emoji: key,
                   amount: value
               })
            });
            that.senders.get(sender).emojis.sort( vocabComparator );
        });
    }
});
