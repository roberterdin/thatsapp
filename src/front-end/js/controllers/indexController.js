App.IndexController = Ember.Controller.extend({
    init : function(){
        console.log('initialising Filey');
        Filey.init();
},


    actions : {
        submitFile : function(){
            console.log('submitFile triggered');
            this._getFileContent();
            this.set('parsing', true);
        },
        /**
         * Clear messages currently in store;
         * TODO: built into interface if not first call
         */
        clearStore : function(){
            // clear the store in case this is not the first call
            // TODO: not working
            this.store.unloadAll('message');
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
        Filey.readText($('#inputFile').get()[0], function(fileContent){
            // example string: 09.06.2013, 4:39 - Sibi: schick meeeeeehh!!

            // hack to match first and last entry
            fileContent = "bla\n" + fileContent;
            fileContent += "00:00 - ";

            var messages = [];

            // splits between date and content
            var firstSplit = fileContent.match(/[\s\S]*?, \d{1,2}:\d{2} - /g);

            // precompilation
            var dateSplitter = /\n(?=[^\n]*$)/;
            var dateTimeSeparator = /, /;
            var matchName = /: /;
            var replaceDash = / - /;

            var splitTmp = [];
            var splitTmp2 = [];

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

                    // split date of previous string
                    splitTmp = firstSplit[i-1].split(dateSplitter);

                    // split between date and time
                    splitTmp2 = splitTmp[1].split(dateTimeSeparator);
                    tmp.date = that._createDate(splitTmp2[0], splitTmp2[1].replace(replaceDash, ''));


                    // match sender' name
                    splitTmp = firstSplit[i].split(matchName);
                    tmp.sender = splitTmp[0];

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
     * @param {string}date
     * @param {string}time
     * @returns {*}
     * @private
     */
    _createDate : function(date, time){
        var resDate;
        if(moment(date).isValid()){
            resDate = moment(date);
        }else if(moment(date, "DD.MM.YYYY").isValid()){
            resDate = moment(date, "DD.MM.YYYY");
        }

        var tmpTime = moment(time, "H:mm");
        resDate.hours(tmpTime.get('hour')).minutes(tmpTime.get('minute'));

        return resDate.toDate();
    }
});