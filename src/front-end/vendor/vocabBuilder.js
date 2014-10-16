var vocabBuilder = (function () {

    // precompiled RegEX
    var stopwordsRE = new RegExp("(?:^|\\s+)(" + stopWords_de_ch.join("|") + ")(?=\\s+|$|[.,!?])", "gi");

    /**
     * Provide a string followed by an arbitrary amount of word --> occurrence maps.
     */
    var build = function (){

        var message = arguments[0].replace(stopwordsRE, ""); // remove stop words
        message = message.toLowerCase();
        message = message.replace(/[^\w\säöüÄÖÜß]/g, ''); //remove all non-alphanumeric
        message = message.replace(/(?:^|\s+)\d+(?=\s+|$|[.,!?])/g, ''); // remove all freestanding numbers, e.g. 2014
        message = message.replace(/(^\s*)|(\s*$)/gi,"");//exclude start and end white-space
        message = message.replace(/\n /,"\n"); // exclude newline with a start spacing
        message = message.replace(/\s{2,}/gi," ");//2 or more space to 1

        // split into words
        var words = message.split(" ");


        // execute for every map provided
        for (var i = 1; i < arguments.length; i++) {
            var currMap = arguments[i];
            if(currMap instanceof Map){

            words.forEach( function (word){
                if(currMap.get(word) !== undefined){ // check if word exists in vocabulary
                    currMap.set(word, currMap.get(word) + 1);
                }else{
                    currMap.set(word, 1);
                }
            } );

            }else{
                console.error("vocabBuilder: argument not instance of Map");
            }
        }
    };


    return {
        build : build
    };
})();