function initData(){
    var script = `
def results = []
sql.eachRow('SELECT * FROM stock') { row ->
    def id = row['id']
    def name = row['name']
    // 将结果添加到列表中
    results << [id: id, name: name]
}
return results`;
    $("#groovyQuery").val(script);
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