<!DOCTYPE html>
<html>
<head>
    <title>Alhambra 1</title>
    <!-- Style related links -->
    <link rel="stylesheet/less" type="text/css" href="resources/less/style.less" />

    <!-- Latest compiled and minified Bootstrap CSS -->
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css"/>
    <script type="application/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
    <script type="application/javascript" src="//netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>

    <script src="//cdnjs.cloudflare.com/ajax/libs/less.js/1.5.0/less.min.js" type="text/javascript"></script>

    <script type="text/javascript" src="resources/test.js"></script>
</head>
<body>
<h2>Hello World!  From views.</h2>
<script type="text/javascript">
    var newURL = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname;
    alert ("We're at "+ newURL);
    testjsinclude();
</script>
<table class="alhlayout">
    <tr id="otherplayers">
        <td class="boardregion">
            <table class="brdtblregion">
                <tr>
                    <td class="otherplayerbox">
                        player 1
                    </td>
                    <td class="otherplayerbox">
                        player 2
                    </td>
                </tr>
            </table>
        </td>

    </tr>
    <tr id="commonboard">
        <td class="boardregion">
            <table class="brdtblregion">
                <tr>
                    <td id="deck">deck</td>
                    <td id="exchange">exchange</td>
                    <td id="market">market</td>
                </tr>
            </table>
        </td>
    </tr>
    <tr id="player">
        <td id="playerbox" class="boardregion">player box</td>
    </tr>

</table>
</body>
</html>