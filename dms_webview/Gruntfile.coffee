"use strict"
LIVERELOAD_PORT = 443
lrSnippet = require("connect-livereload")(port: LIVERELOAD_PORT)

# var conf = require('./conf.'+process.env.NODE_ENV);
mountFolder = (connect, dir) ->
    connect.static require("path").resolve(dir)


# # Globbing
# for performance reasons we're only matching one level down:
# 'test/spec/{,*}*.js'
# use this if you want to recursively match all subfolders:
# 'test/spec/**/*.js'
module.exports = (grunt) ->
    require("load-grunt-tasks") grunt
    require("time-grunt") grunt

    # configurable paths
    yeomanConfig =
        app: "client"
        dist: "dist"
        docs: "documentation"

    try
        yeomanConfig.app = require("./bower.json").appPath or yeomanConfig.app

    VERSION = grunt.template.today("yymmddHHMM")
    params = {
        version: VERSION
    }
    grunt.initConfig
        yeoman: yeomanConfig
        params: params
        watch:
            coffee:
                files: ["<%= yeoman.app %>/scripts/**/*.coffee"]
                tasks: ["coffee:dist"]

            less:
                files: ["<%= yeoman.app %>/styles-less/**/*.less"]
                tasks: ["less:server"]

            livereload:
                options:
                    livereload: LIVERELOAD_PORT

                files: [
                    "<%= yeoman.app %>/index.html"
                    "<%= yeoman.app %>/views/**/*.html"
                    "<%= yeoman.app %>/styles/**/*.less"
                    ".tmp/styles/**/*.css"
                    "{.tmp,<%= yeoman.app %>}/scripts/**/*.js"
                    "<%= yeoman.app %>/images/**/*.{png,jpg,jpeg,gif,webp,svg}"
                ]

        connect:
            options:
                protocol: 'https'
                port: 443

                # Change this to '0.0.0.0' to access the server from outside.
                hostname: "192.168.99.184"
                
                key: grunt.file.read('ssl/key.pem'),
                cert: grunt.file.read('ssl/cert.pem')

            livereload:
                options:
                    middleware: (connect) ->
                        [lrSnippet, mountFolder(connect, ".tmp"), mountFolder(connect, yeomanConfig.app)]

            test:
                options:
                    middleware: (connect) ->
                        [mountFolder(connect, ".tmp"), mountFolder(connect, "test")]

            dist:
                options:
                    middleware: (connect) ->
                        [mountFolder(connect, yeomanConfig.dist)]

        open:
            server:
                url: "192.168.99.184:<%= connect.options.port %>"

        clean:
            dist:
                files: [
                    dot: true
                    src: [".tmp", "<%= yeoman.dist %>/*", "!<%= yeoman.dist %>/.git*"]
                ]
            all: [
                ".tmp"
                "client/bower_components"
                "node_modules"
                ".git"
            ]
            server: ".tmp"

        jshint:
            options:
                jshintrc: ".jshintrc"

            all: ["Gruntfile.js", "<%= yeoman.app %>/scripts/**/*.js"]

        less:
            server:
                options:
                    strictMath: true
                    dumpLineNumbers: true
                    sourceMap: true
                    sourceMapRootpath: ""
                    outputSourceFiles: true
                files: [
                    expand: true
                    cwd: "<%= yeoman.app %>/styles-less"
                    src: "main.less"
                    dest: ".tmp/styles"
                    ext: ".css"
                ]
            dist:
                options:
                    cleancss: true
                    compress: true
                    report: 'min'
                files: [
                    expand: true
                    cwd: "<%= yeoman.app %>/styles"
                    src: "main.less"
                    dest: ".tmp/styles"
                    ext: ".css"
                ]
            dev:
                options:
                    cleancss: true
                    compress: true
                    report: 'min'
                files: [
                    expand: true
                    cwd: "<%= yeoman.app %>/styles"
                    src: "main.less"
                    dest: "<%= yeoman.app %>/styles"
                    ext: ".css"
                ]


        coffee:
            server:
                options:
                    sourceMap: true
                    # join: true,
                    sourceRoot: ""
                files: [
                    expand: true
                    cwd: "<%= yeoman.app %>/scripts"
                    src: "**/*.coffee"
                    dest: ".tmp/scripts"
                    ext: ".js"
                ]
            dist:
                options:
                    sourceMap: false
                    sourceRoot: ""
                files: [
                    expand: true
                    cwd: "<%= yeoman.app %>/scripts"
                    src: "**/*.coffee"
                    dest: ".tmp/scripts"
                    ext: ".js"
                ]
            dev:
                options:
                    sourceMap: false
                files: [
                    expand: true
                    cwd: "<%= yeoman.app %>/scripts"
                    src: "**/*.coffee"
                    dest: "<%= yeoman.app %>/scripts"
                    ext: ".js"
                ]

        useminPrepare:
            html: ".tmp/index.html"
            options:
                dest: "<%= yeoman.dist %>"
                flow:
                    steps:
                        js: ["concat"]
                        css: ["cssmin"]
                    post: []


        # 'css': ['concat']
        usemin:
            html: ["<%= yeoman.dist %>/**/*.html", "!<%= yeoman.dist %>/bower_components/**"]
            css: ["<%= yeoman.dist %>/styles/**/*.css"]
            options:
                dirs: ["<%= yeoman.dist %>"]

        htmlmin:
            dist:
                options: {}
                files: [
                    expand: true
                    cwd: "<%= yeoman.app %>"
                    src: ["*.html"]
                    dest: "<%= yeoman.dist %>"
                ]

        # Put files not handled in other tasks here
        copy:
            dist:
                files: [
                    expand: true
                    dot: true
                    cwd: "<%= yeoman.app %>"
                    dest: "<%= yeoman.dist %>"
                    src: [
                        "favicon.ico"
                        # bower components that has image, font dependencies
                        "bower_components/font-awesome/css/*"
                        "bower_components/font-awesome/fonts/*"
                        "bower_components/weather-icons/css/*"
                        "bower_components/weather-icons/fonts/*"
                        "bower_components/weather-icons/font/*"

                        "fonts/**/*"
                        "i18n/**/*"
                        "images/**/*"
                        # "styles/bootstrap/**/*"
                        "styles/fonts/**/*"
                        "styles/img/**/*"
                        "styles/ui/images/*"
                        "styles/directive/images/*"
                        # Comment line below if using ng-template
                        # "views/**/*"
                    ]
                ,
                    expand: true
                    cwd: ".tmp"
                    dest: "<%= yeoman.dist %>"
                    src: ["styles/**", "assets/**"]
                ,
                    expand: true
                    cwd: ".tmp/images"
                    dest: "<%= yeoman.dist %>/images"
                    src: ["generated/*"]
                ]

            styles:
                expand: true
                cwd: "<%= yeoman.app %>/styles"
                dest: ".tmp/styles/"
                src: "**/*.css"

        concurrent:
            server: ["coffee:server", "less:server", "copy:styles"]
            dist: ["coffee:dist", "less:dist", "copy:styles", "htmlmin"]

        cssmin:
            options:
                keepSpecialComments: '0'
            dist: {}    # usemin takes care of that

        concat:
            options:
                separator: grunt.util.linefeed + ';' + grunt.util.linefeed
            dist: {}   # usemin takes care of that
            template: {
                files: {
                    "<%= yeoman.dist %>/scripts/app.js": [
                        "<%= yeoman.dist %>/scripts/app.js"
                        ".tmp/template.js"
                    ]
                }
            }

        uglify:
            options:
                mangle: false
                compress:
                    drop_console: true
            dist:
                files:
                    "<%= yeoman.dist %>/scripts/app.js": ["<%= yeoman.dist %>/scripts/app.js"]

        ngtemplates: {
            app: {
                cwd: "<%= yeoman.app %>"
                src: [
                    "views/*.html"
                    "views/**/*.html"
                ]
                dest: '.tmp/template.js'
                options: {
                    module: "app"
                    standalone: false
                    append: false
                    htmlmin: {
                        collapseWhitespace: true
                    }
                }
            }
        },
        replace: {
            versioning: {
                options: {
                    patterns: [
                        {
                            match: /styles\/(\w+)\.css/g,
                            replacement: 'styles/$1.css?v=' + VERSION
                        },
                        {
                            match: /scripts\/(\w+)\.js/g,
                            replacement: 'scripts/$1.js?v=' + VERSION
                        }
                    ]
                },
                files: [
                    {
                        expand: true,
                        flatten: true,
                        src: ['<%= yeoman.dist %>/index.html'],
                        dest: '<%= yeoman.dist %>'}
                ]
            }
            angularProduction: {
                options: {
                    patterns: [
                        {
                            match: /\.run/g,
                            replacement: '.config([\'$compileProvider\', function ($compileProvider) { $compileProvider.debugInfoEnabled(false); }])\r\n.run'
                        },
                        {
                            match: /angular\.js/g,
                            replacement: 'angular.min.js'
                        }
                    ]
                },
                files: [
                    {
                        expand: true,
                        flatten: true,
                        src: ['.tmp/scripts/app.js'],
                        dest: '.tmp/scripts'}
                ]
            }
            includeMin: {
                options: {
                    patterns: [
                        {
                            match: /angular\.js/g,
                            replacement: 'angular.min.js'
                        }
                    ]
                },
                files: [
                    {
                        expand: true,
                        flatten: true,
                        src: ['<%= yeoman.app %>/index.html'],
                        dest: '.tmp'}
                ]
            }
        }

    grunt.registerTask "server", (target) ->
        return grunt.task.run(["serve:dist"])  if target is "dist"
        grunt.task.run(["serve"])

    grunt.registerTask "serve", (target) ->
        return grunt.task.run(["build", "open", "connect:dist:keepalive"])  if target is "dist"
        grunt.task.run ["clean:server", "concurrent:server", "connect:livereload", "open", "watch"]

    grunt.registerTask "build", ["clean:dist", "replace:includeMin", "useminPrepare", "concurrent:dist",
                                 "replace:angularProduction", "copy:dist", "concat:generated",
                                 "usemin", "ngtemplates", "concat:template", "uglify:dist", "replace:versioning"]

    grunt.registerTask "default", ["server"]
