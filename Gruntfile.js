module.exports = function(grunt) {
  grunt.initConfig({
    stylus: {
      compile: {
        options: {
          compress: false
        },
        files: {
          'resources/public/css/site.css' : ['styles/**/*.styl']
        },
      }
    },
    watch : {
      files: ['styles/**/*.styl'],
      tasks: 'stylus'
    }
  });
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-stylus');
};
