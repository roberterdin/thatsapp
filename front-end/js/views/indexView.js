App.IndexView = Ember.View.extend({
    templateName: 'index',

    didInsertElement : function(){
        this._super();
        Ember.run.scheduleOnce('afterRender', this, function(){
            $('input[type=file]').bootstrapFileInput();
            $('.file-inputs').bootstrapFileInput();
        });
    }
});