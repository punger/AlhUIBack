<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alhambra 1</title>
    <!-- Style related links -->
    <link rel="stylesheet/less" type="text/css" href="resources/less/style.less" />

    <!-- Latest compiled and minified Bootstrap CSS -->
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <script type="application/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
    <script type="application/javascript" src="//netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>

    <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/less.js/1.5.0/less.min.js"></script>
    <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
    <script type="application/javascript" src="resources/libs/pure.js"></script>

    <script type="text/javascript" src="resources/playerselect.js"></script>
</head>
<body>
<script type="text/javascript">
$(function() {
    $.getJSON('fullplayerlist', function (resp) {
        console.log(JSON.stringify(resp));
        getplayerlist('#anchor', resp, function(players) {
            console.log('player dialog returned');
            $.ajax("startgame",
                {
                    data: { "players": players},
                    dataType: "json",
                    traditional: true,
                    success: function (result) {
                        alert(result);
                    },
                    error: function (jqXHR, textStatus, errorThrown ) {
                        alert ("Start game failed: '"+textStatus+"', err'"+errorThrown+"'");
                    }
                }
            );
//            $.getJSON("startgame", { "players": players}, function (result) {
//                alert(result);
//            });
        });
    });

});

</script>

<div class="container-fluid">
    <div id="anchor"></div>

    <div id="otherplayers" class="row brdtblregion">
        <div class="otherplayerbox col-md-6">
            player 1
        </div>
        <div class="otherplayerbox col-md-6">
            player 2
        </div>
    </div>
    <div id="commonboard" class="row brdtblregion">
        <div id="deck" class="col-md-2">deck <img src="resources/images/blue2.png"/> </div>
        <div id="exchange" class="col-md-4">exchange</div>
        <div id="market" class="col-md-6">market</div>
    </div>
    <div id="player" class="row brdtblregion">
        <div id="playermat" class="col-md-12">player</div>
    </div>
</div>
</body>
</html>