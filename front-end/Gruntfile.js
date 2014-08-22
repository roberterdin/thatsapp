var LESS_FILES = [
    'less/bootstrap/bootstrap.less',
    'less/main.less'];

var JS_FILES = [
    'js/vendor/jquery-2.1.1.js',
    'js/vendor/bootstrap/*.js',
    'js/vendor/handlebars-1.1.2.js',
    'js/vendor/ember-1.7.0.js',
    'js/vendor/ember-data.js',
    'js/app.js',
    'js/router.js',
    'js/controllers/*.js',
    'js/models/*.js'];


var JS_FILES_DEV = JS_FILES.slice(0);
JS_FILES_DEV.push('tests/runner.js');


module.exports = function (grunt) {

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        jshint: {
            options: {
                shadow: true,
                sub: true
            },
            all: ['js/**/*.js', '!js/vendor/**/*', '!js/bin/**/*']
        },

        replace: {
            all: {
                src: ['index.src.html'],
                dest: 'index.html',
                replacements: [{
                    from: "%version%",
                    to: "<%= pkg.version %>"
                },
                {
                    from: "%shortname%",
                    to: "<%= pkg.shortname %>"
                }

                ]
            }
        },

        uglify: {
            options: {
                banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',
                sourceMap: true,
                mangle: {
                    except: ['jQuery', 'Backbone', 'Underscore', 'Ember']
                }
            },
            dev: {
                options: {
                    mangle: false,
                    compress: false
                },
                files: {
                    '../../bin/dev/front-end/js/bin/<%= pkg.shortname %>.<%= pkg.version %>.min.js': JS_FILES_DEV
                }
            },
            prod: {
                options: {
                    sourceMap: false
                },
                files: {
                    '../../bin/prod/front-end/js/bin/<%= pkg.shortname %>.<%= pkg.version %>.min.js': JS_FILES
                }
            }
        },


        less: {
            dev: {
                options: {
                    paths: ["css"],
                    sourceMap: true,
                    compress: false,
                    cleancss: false
                },
                files: {
                    '../../bin/dev/front-end/css/<%= pkg.shortname %>.<%= pkg.version %>.css': LESS_FILES
                }
            },
            prod: {
                options: {
                    paths: ["css"],
                    sourceMap: false,
                    compress: true,
                    cleancss: true
                },
                files: {
                    '../../bin/prod/front-end/css/<%= pkg.shortname %>.<%= pkg.version %>.css': LESS_FILES
                }
            }
        },


        emberTemplates: {
            dev: {
                options: {
                    templateBasePath: /templates\//
                },
                files: {
                    "../../bin/dev/front-end/js/bin/templates.<%= pkg.version %>.min.js": "templates/**/*.hbs"
                }
            },
            prod: {
                options: {
                    templateBasePath: /templates\//
                },
                files: {
                    "../../bin/prod/front-end/js/bin/templates.<%= pkg.version %>.min.js": "templates/**/*.hbs"
                }
            }
        },


        htmlmin: {
            dev: {
                files: {
                    '../../bin/dev/front-end/index.html': 'index.html'
                }
            },
            prod: {
                options: {
                    removeComments: true,
                    collapseWhitespace: true
                },
                files: {
                    '../../bin/prod/front-end/index.html': 'index.html'
                }
            }
        },


        imagemin: {                          // Task
            prod: {                         // Another target
                files: [
                    {
                        expand: true,                  // Enable dynamic expansion
                        //cwd: 'src/',                   // Src matches are relative to this path
                        src: ['img/**/*.{png,jpg,gif}'],   // Actual patterns to match
                        dest: '../../bin/prod/front-end'                  // Destination path prefix
                    }
                ]
            }
        },

        copy: {
            dev: {
                files: [
                    {expand: true, src: ['img/**/*'], dest: '../../bin/dev/front-end/', filter: 'isFile'},
                    {expand: true, src: ['fonts/**/*'], dest: '../../bin/dev/front-end/', filter: 'isFile'},
                    {expand: true, src: ['.htaccess', '404.html', 'crossdomain.xml', 'favicon.ico', 'robots.txt'], dest: '../../bin/dev/front-end/', filter: 'isFile'},
                    {expand: true, src: ['tests/**/*'], dest: '../../bin/dev/front-end/', filter: 'isFile'},
                    {expand: true, src: ['js/vendor/modernizr-2.6.2.min.js'], dest: '../../bin/dev/front-end/', filter: 'isFile'},
                    {expand: true, src: ['../*'], dest: '../../bin/dev/page', filter: 'isFile'},
                    {expand: true, src: ['../php/*'], dest: '../../bin/dev/php', filter: 'isFile'}

                ]
            },
            prod: {
                files: [
                    //{expand: true, src: ['img/**/*'], dest: '../bin/prod/', filter: 'isFile'},
                    {expand: true, src: ['fonts/**/*'], dest: '../../bin/prod/front-end/', filter: 'isFile'},
                    {expand: true, src: ['.htaccess', '404.html', 'crossdomain.xml', 'favicon.ico'], dest: '../../bin/prod/front-end/', filter: 'isFile'},
                    {expand: true, src: ['js/vendor/modernizr-2.6.2.min.js'], dest: '../../bin/prod/front-end/', filter: 'isFile'},
                    {expand: true, src: ['../*'], dest: '../../bin/prod/front-end', filter: 'isFile'},
                    {expand: true, src: ['../php/*'], dest: '../../bin/prod/php', filter: 'isFile'}
                ]
            }
        },


        'ftp-deploy': {
            prod: {
                auth: {
                    host: 'ftp.whatisticswhatistics.ch',
                    port: 21,
                    authKey: 'key1'
                },
                src: '../../bin/prod/',
                dest: 'whatisticswhatistics.ch/',
                exclusions: ['../../bin/prod/ws', '../../bin/prod/**/.DS_Store']
            },
            dev: {
                auth: {
                    host: 'ftp.whatisticswhatistics.ch',
                    port: 21,
                    authKey: 'key1'
                },
                src: '../../bin/prod/',
                dest: 'dev.whatisticswhatistics.ch/',
                exclusions: ['../../bin/prod/ws', '../../bin/prod/**/.DS_Store', 'robots.txt']
            }
        }
    });



    grunt.loadNpmTasks('grunt-text-replace');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-ftp-deploy');
    grunt.loadNpmTasks('grunt-contrib-htmlmin');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-ember-templates');
    grunt.loadNpmTasks('grunt-contrib-imagemin');


    // Default task(s).
    grunt.registerTask('default', ['jshint', 'replace:all', 'uglify:dev', 'less:dev', 'htmlmin:dev', 'copy:dev', 'emberTemplates:dev']);

    // Production (build)
    grunt.registerTask('prod', ['jshint', 'replace:all', 'uglify:prod', 'less:prod', 'htmlmin:prod', 'copy:prod', 'emberTemplates:prod', 'imagemin:prod']);

    // Deploy
    grunt.registerTask('deploy-dev', ['ftp-deploy:dev']);
//    grunt.registerTask('deploy-prod', ['ftp-deploy:prod']);

};