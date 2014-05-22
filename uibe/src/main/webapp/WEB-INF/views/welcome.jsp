<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alhambra 1</title>

    <!-- Latest compiled and minified Bootstrap CSS -->
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css"/>

    <!-- Style related links -->
    <link rel="stylesheet/less" type="text/css" href="resources/less/style.less" />


    <!--<script type="application/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>-->
    <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/less.js/1.7.0/less.min.js"></script>
    <!--<script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>-->

    <script type="application/javascript" src="bower_components/jquery/dist/jquery.js"></script>
    <script type="application/javascript" src="bower_components/jquery-ui/ui/jquery-ui.js"></script>
    <script type="application/javascript" src="bower_components/beebole-pure/libs/pure.js"></script>
    <script type="application/javascript" src="bower_components/async/lib/async.js"></script>

    <script type="application/javascript" src="//netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>

    <script type="application/javascript" src="resources/playerselect.js"></script>
    <script type="application/javascript" src="resources/game.js"></script>
    <script type="application/javascript" src="resources/player.js"></script>
    <script type="application/javascript" src="resources/card.js"></script>
    <script type="application/javascript" src="resources/tile.js"></script>
    <script type="application/javascript" src="resources/board.js"></script>
    <script type="application/javascript" src="resources/market.js"></script>
    <script type="application/javascript" src="resources/exchange.js"></script>
    <script type="application/javascript" src="resources/cardset.js"></script>
    <!--<script type="application/javascript" src="resources/reserve.js"></script>-->
    <!--
    <script type="application/javascript" src="resources/main.js"></script>
    -->
    <script type="application/javascript" src="resources/jqui/jqmarket.js"></script>
    <script type="application/javascript" src="resources/jqui/jqselectable.js"></script>
    <script type="application/javascript" src="resources/jqui/jqexchange.js"></script>
    <script type="application/javascript" src="resources/jqui/jqplayer.js"></script>
    <script type="application/javascript" src="resources/jqui/jqcardset.js"></script>
    <script type="application/javascript" src="resources/jqui/jqtileset.js"></script>
    <script type="application/javascript" src="resources/jqui/jqhand.js"></script>
    <script type="application/javascript" src="resources/jqui/jqactions.js"></script>
    <script type="application/javascript" src="resources/jqui/jqgame.js"></script>
    <script type="application/javascript" src="resources/jqui/jqtileset.js"></script>
    <script type="application/javascript" src="resources/jqui/jqboard.js"></script>
</head>
<body>
<script type="text/javascript">
$(function() {
    var game;
    var marketui;
    var exchangeui;
    var playerui;
    var gameui;
    var roster;

    async.waterfall(
        [
            function(cb) {
                $.getJSON('fullplayerlist', function (resp) {
                    console.log(JSON.stringify(resp));
                    cb(null, resp);
                });
            },
            function (fpResp, cb) {
                getplayerlist('#anchor', fpResp,
                        function(players) {
                            console.log('player dialog returned' + JSON.stringify(players));
                            roster = players;
                            cb(null, players);
                        });
            },
            function(players, cb) {
                if (!players || players.length == 0) {
                    players = ["black", "red", "orange"];
                }
                $.ajax("startgame",
                    {
                        data: { "players": players},
                        dataType: "json",
                        traditional: true,
                        success: function (result) {
                            if (!result) {
                                cb("Empty return from server.");
                            }
                            if (!result.success) {
                                cb("Server error: "+result.message);
                            }
                            var startPlayer = result.player;
                            // Reorder the players so that the start player is in the first entry
                            for (var i = 0; i < roster.length; i++) {
                                if (roster[i] === startPlayer) {
                                    if (i > 0) {
                                        roster = roster.slice(i).concat(roster.slice(0, i));
                                    }
                                    break;
                                }
                            }
                            game = new Game(players,startPlayer,cb);
                        },
                        error: function (jqXHR, textStatus, errorThrown ) {
                            cb("Start game failed: '"+textStatus+"', err'"+errorThrown+"'");
                        }
                    }
                );
            },
            function(gameres, cb) {
                async.parallel({
                    "showMkt": function(cb1) {
                        async.series(
                            [
                                function(cb2) {
                                    marketui = new MarketJQ(game, "#market", cb2);
                                },
                                function(cb2) {
                                    marketui.show();
                                    cb2(null)
                                }
                            ],
                            function(err) {
                                cb1(err, marketui);
                            }
                        );
                    },
                    "showXchg": function(cb1) {
                        async.series(
                            [
                                function(cb2) {
                                    exchangeui = new ExchangeJQ(game, "#exchange", cb2);
                                },
                                function(cb2) {
                                    exchangeui.show();
                                    cb2(null)
                                }
                            ],
                            function(err) {
                                cb1(err, exchangeui);
                            }
                        );
                    }
                },
                function(err, res) {
                    cb(err, res);
                });
            },
            function(builtuis, cb) {
                playerui = new PlayerJQ(
                        game, game.curplayer, "#playerbox0", cb);
            },
            function(puis, cb) {

                gameui = new GameJQ(game, {
                    "m": marketui,
                    "x": exchangeui,
                    "p": playerui
                });
                cb();
            }
        ],
        function(err, result) {
            if (err) alert(err);
        }
    );
    console.log("fell out of asynch loop");

});

</script>


<div class="main">
    <div id="anchor"></div>

    <table id="maindisplay">
        <tr>
            <td id="otherplayers">
                <table>
                    <tr>
                        <td>
                            <div id="playerbox1" class="otherplayerbox">
                                player 1
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div id="playerbox2" class="otherplayerbox">
                                player 2
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
            <td id="mainboard">
                <table>
                    <tr>
                        <td>
                            <div id="exchange" class="nopadding">exchange</div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div id="market" class="nopadding">market</div>
                        </td>
                    </tr>
                </table>
            </td>
            <td id="playerbox0"></td>
        </tr>
    </table>
</div>
<!--<div class="container-fluid">-->
    <!--<div id="anchor"></div>-->

    <!--<div class="row-fluid brdtblregion">-->
        <!--<div  id="otherplayers" class="col-md-2">-->
            <!--<div id="playerbox1" class="otherplayerbox">-->
                <!--player 1-->
            <!--</div>-->
            <!--<div id="playerbox2" class="otherplayerbox">-->
                <!--player 2-->
            <!--</div>-->

        <!--</div>-->
        <!--<div id="mainboard" class="col-md-4">-->
            <!--<div id="exchange" class="nopadding">exchange</div>-->
            <!--<div id="market" class="nopadding">market</div>-->
        <!--</div>-->
        <!--<div id="playerbox0" class="col-md-5">-->
        <!--</div>-->
    <!--</div>-->
<!--</div>-->
</body>
</html>