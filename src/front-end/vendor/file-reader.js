/**
 * @author: Robert Erdin (erdi@zhaw.ch)
 * @version v0.1
 */
/* global Filey */
var Filey = Filey || (function() {


    var _init = false;
    var _reader;

    return{
        /**
         * Check for file API support
         * @returns {boolean}
         */
        init : function(){
            if (window.File && window.FileReader && window.FileList && window.Blob) {
                _reader = new FileReader();
                _init = true;
                return true;
            } else {
                console.log('The File APIs are not fully supported by your browser. Fallback required.');
                _init = true;
                return false;
            }
        },



        readText : function(filePath, callback){
            if(_init){
                var output = ""; //placeholder for text output
                if(filePath.files && filePath.files[0]) {
                    _reader.onload = function (e) {
                        output = e.target.result;
                        callback(output);
                    };//end onload()
                    _reader.readAsText(filePath.files[0]);
                }//end if html5 filelist support
                else if(ActiveXObject && filePath) { //fallback to IE 6-8 support via ActiveX (UNTESTED)
                    try {
                        _reader = new ActiveXObject("Scripting.FileSystemObject");
                        var file = _reader.OpenTextFile(filePath, 1); //ActiveX File Object
                        output = file.ReadAll(); //text contents of file
                        file.Close(); //close file "input stream"
                        callback(output, context);
                    } catch (e) {
                        if (e.number == -2146827859) {
                            alert('Unable to access local files due to browser security settings. ' +
                                'To overcome this, go to Tools->Internet Options->Security->Custom Level. ' +
                                'Find the setting for "Initialize and script ActiveX controls not marked as safe" and change it to "Enable" or "Prompt"');
                        }
                    }
                }
                else { // possible fallback point for Java applet or flash.
                    return false;
                }
                return true;
            } else {
                console.log('Initialise first');
            }

        }
    };
})();

