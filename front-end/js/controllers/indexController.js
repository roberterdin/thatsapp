App.IndexController = Ember.Controller.extend({
    init : function(){
        console.log('initialising Filey');
        Filey.init();
    },


    actions : {
        submitFile : function(){
            console.log('submitFile triggered');
            this._getFileContent();
            this.set('parsed', true);
        }
    },

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
//            var matchName = /.*(?=: )/;
            var matchName = /: /;
            var replaceDash = / - /;

            var splitTmp = [];
            var splitTmp2 = [];

            for(var i = 1; i < firstSplit.length; i++){
                var tmp = {};

                splitTmp = firstSplit[i-1].split(dateSplitter);
                splitTmp2 = splitTmp[1].split(dateTimeSeparator);
                tmp.date = splitTmp2[0];
                tmp.time = splitTmp2[1].replace(replaceDash, '');


                splitTmp = firstSplit[i].split(matchName);
                tmp.sender = splitTmp[0];


                tmp.content = splitTmp[1].split(dateSplitter)[0];

                console.log(tmp);
                messages.push(tmp);
            }
            console.log('all records parsed');
            that.set('messages', messages);
        });
    }

});