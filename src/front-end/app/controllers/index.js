import Ember from 'ember';
//import Filey from 'file-reader';
//import moment from 'moment';
/* global moment */
/* global Filey */

export default Ember.Controller.extend({

    init : function(){
        console.log('initialising Filey');
        Filey.init();
    },


    actions : {
        submitFile : function(){
            console.log('submitFile triggered');
            this.store.unloadAll('message');
            this._getFileContent();
            this.set('parsing', true);
        }
    },

    /**
     * Gets called by the router each time the index route is deactivated
     * @returns {boolean}
     * @private
     */
    _reset : function(){
        this.set('parseProgress', 0);
        this.set('parsing', false);
        this.set('parsed', false);
        return true;
    },

    parseProgress : 0,

    widthStyle: function() {
        return 'width: ' + this.get('parseProgress') + '%;';
    }.property('parseProgress'),


    /**
     * Get file content and parse it.
     * parsed output: date | time | sender | message
     * @private
     */
    _getFileContent : function(){
        console.log('Trying to retrieve file content...');
        var that = this;
        Filey.readText(Ember.$('#inputFile').get()[0], function(fileContent){
            // example string: 09.06.2013, 4:39 - Sibi: schick meeeeeehh!!

            // hack to match first and last entry
            fileContent = "bla\n" + fileContent;
            fileContent += "00:00 - ";

            // remove empty lines
            fileContent.replace(/^\n/gm, "");

            // splits between date and content
            var firstSplit = fileContent.match(/[\s\S]*?\n[\s\w.]{1,14} \d{1,2}:\d{2}:{0,1}\d{0,2}( - | (AM|PM) - |:\s)/g);


            // precompilation
            var dateSplitter = /\n[^\n]{1,14} \d{1,2}:\d{2}:{0,1}\d{0,2}( - | (AM|PM) - |:\s)/;
            var newLine = /\n/;
            var matchName = /: /;

            var splitTmp = [];

            // interval is necessary not to mess with the ember run loop:
            // http://emberjs.com/guides/understanding-ember/run-loop/
            // TODO: tweak interval time
            var pointer = 1;
            var endOfLoop = 0;
            var finalInterval = false;
            var interval = setInterval(function(){

                if ((pointer + 500) < firstSplit.length){
                    endOfLoop = pointer + 500;
                }else{
                    endOfLoop = firstSplit.length;
                    finalInterval = true;
                }

                for(var i = pointer; i <endOfLoop; i++){
                    var tmp = {};

                    tmp.date = that._createDate(firstSplit[i-1].match(dateSplitter)[0].replace(newLine, ""));

                    // match sender' name
                    splitTmp = firstSplit[i].split(matchName);
                    tmp.sender = splitTmp[0];

                    // filter out lines like:
                    // Robi hat den Betreff zu “workoholics & serienjunks” geändert
                    // TODO: handle on android (not severe there because it does not create additional senders)
                    if(splitTmp[1] === ""){
                        console.log("Skipped info line: " + firstSplit[i-1]);
                        continue;
                    }



                    // get content
                    tmp.content = splitTmp[1].split(dateSplitter)[0];

                    that.store.createRecord('message', tmp);

                    //update progress
                    that.set('parseProgress', Math.ceil(i/firstSplit.length*100));
                    that.notifyPropertyChange('widthStyle');
                }

                pointer = i;

                if(finalInterval){
                    clearInterval(interval);
                    that.set('parsing', false);
                    that.set('parsed', true);
                    console.log('all records parsed');
                    console.log('transitioning to results...');
                    that.transitionToRoute('results.index');
                }
            }, 200);
        });
    },

    /**
     * Create a JavaSript date object
     * @param {string}rawDateTime
     * @returns {*}
     * @private
     */
    _createDate : function(rawDateTime){
        var resDate;
        if(moment(rawDateTime).isValid()){
            resDate = moment(rawDateTime);
        }else if(moment(rawDateTime, "D. MMM. HH:mm").isValid){ // android-de-24h: 1. Mär. 07:17
            resDate = moment(rawDateTime, "D. MMM. HH:mm");
        }else if(moment(rawDateTime, "D. MMM., H:mm").isValid()){ // android-de: 16. Jan., 14:40 | 9. Mär., 9:49
            resDate = moment(rawDateTime, "D. MMM., H:mm");
        }else if(moment(rawDateTime, "DD.MM.YYYY, H:mm").isValid()){ // android-de: 05.12.2013, 9:59
            resDate = moment(rawDateTime, "DD.MM.YYYY, H:mm");
        }else if(moment(rawDateTime, "MMM D, HH:mm").isValid()){ // android-en_us-24h: Aug 3, 02:38
            resDate = moment(rawDateTime, "MMM D, HH:mm");
        }else if(moment(rawDateTime, "D MMM HH:mm").isValid()){ // android-en_gb-24h: 1 Mar 06:52
            resDate = moment(rawDateTime, "D MMM HH:mm");
        }else if(moment(rawDateTime, "D MMM YYYY HH:mm").isValid()) { // android-en_gb-24h: 28 Dec 2013 05:09
            resDate = moment(rawDateTime, "D MMM HH:mm");
        }else if(moment(rawDateTime, "DD.MM.YY HH:mm:ss").isValid()){ // ios_de 20.12.13 16:48:24

        }else{
            console.log("No known date: " + rawDateTime);
        }

        return resDate.toDate();
    }
});