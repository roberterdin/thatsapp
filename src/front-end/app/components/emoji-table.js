import Ember from 'ember';

export default Ember.Component.extend({

    filteredEmoticons: Ember.computed('model', function(){
        var result = Ember.$.map(this.get('model'), (k,v) => {
            return {unicode: v, amount: k};
        }).sort((a, b) => {
            if (a.amount < b.amount){
                return 1;
            }
            if ( a.amount > b.amount){
                return -1;
            }
            return 0;
        }).slice(0,9);

        result.forEach(emoji => {
            emoji.html = emojione.unicodeToImage(emoji.unicode);
        });

        return result;

    })
});
