/**
 * @class
 * @param name
 * @constructor
 */
function Sender(name){
    this.name = name;
    this.messageAmount = 0;
    this.wordAmount = 0;
}

function GlobalStat(){
    this.senderAmount = 0; //TODO: redundant information with senders.size()
    this.messageAmount = 0;
    this.wordAmount = 0;
}

App.ResultsController = Ember.ObjectController.extend({
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

        var that = this;
        this.get('model.messages').forEach(function(message){

            // create sender if not already existing
            if(that.get('senders').get(message.get('sender')) === undefined){
                that.get('senders').set(message.get('sender'), new Sender(message.get('sender')));
                that.get('globalStat').senderAmount++;
            }

            var tmpWordCount = countWords(message.get('content'));

            // global adjustments
            that.get('globalStat').messageAmount++;
            that.get('globalStat').wordAmount += tmpWordCount;


            // sender adjustments
            that.get('senders').get(message.get('sender')).messageAmount++;
            that.get('senders').get(message.get('sender')).wordAmount += tmpWordCount;

            // generate history
            var date = moment(message.get('date'));
            if(that.get('history').has(date.format('DD.MM.YY')) === false){
                that.get('history').set(date.format('DD.MM.YY'), 1);
            }else{
                that.get('history').set(date.format('DD.MM.YY'), that.get('history').get(date.format('DD.MM.YY')) + 1);
            }
        });
    }
});