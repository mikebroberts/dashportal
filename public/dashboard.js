
var lastResponse = "";

$(function() {
    function worker() {
        $.ajax({
            url: ('/api/urls/' + $(document).find("title").text()),
            success: function(data) {
                console.log(data);
                if (lastResponse.localeCompare(data) != 0) {
                    console.log("New url! " + data);
                    lastResponse = data;
                    document.getElementById("theiframe").src = lastResponse;
                }
            },
            complete: function() {
                setTimeout(worker, 2000);
            }
        });
    }
    worker();
});

