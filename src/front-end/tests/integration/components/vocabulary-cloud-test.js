import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('vocabulary-cloud', 'Integration | Component | vocabulary cloud', {
  integration: true
});

test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{vocabulary-cloud}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#vocabulary-cloud}}
      template block text
    {{/vocabulary-cloud}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});
