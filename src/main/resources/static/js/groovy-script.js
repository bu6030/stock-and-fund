function initData() {
    $.ajax({
        url: '/groovy-script',
        type: 'GET',
        data: {},
        success: function(data) {
            script = data;
            $("#groovyQuery").val(script);
        },
        error: function(error) {
            $('#result').text('Error: ' + error.responseText);
        }
     });
}

function submitQuery() {
          var query = $('#groovyQuery').val();
          $.ajax({
              url: '/execute-query',
              type: 'POST',
              data: { query: query },
              success: function(data) {
                  $('#result').text('Result: ' + data);
              },
              error: function(error) {
                  $('#result').text('Error: ' + error.responseText);
              }
          });
}