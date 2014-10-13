/**
 * Counts the amount of words in a string.
 * @param {string}s
 * @returns {Array.length|*}
 */
function countWords(s){
    s = s.replace(/(^\s*)|(\s*$)/gi,"");//exclude start and end white-space
    s = s.replace(/[ ]{2,}/gi," ");//2 or more space to 1
    s = s.replace(/\n /,"\n"); // exclude newline with a start spacing
    var matches = s.match(/[\w\d]+/gi);
    return matches ? matches.length : 0;
}